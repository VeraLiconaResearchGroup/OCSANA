package org.cytoscape.myapp.internal.Drugeability;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mkordi
 */
public class SavepdbFile {

    //shows  PDB website url
    private String uRL = "http://www.rcsb.org/pdb/files/";

    // shows address of file that we want to save PDB file
    private String fileAddress;

    private String pdbID = null;

    public SavepdbFile(String fileAddress) {

        this.fileAddress = fileAddress;
    }

    public SavepdbFile(String URL, String fileAddress) {
        this.uRL = URL;
        this.fileAddress = fileAddress;
    }

    /**
     * input:pdfID, Id of protein
     *
     * create file on fileAddress folder with namr id.txt and get onformation
     * from URL and write on txt file
     *
     * @return address of txt file that created by this method.
     */
    public String writePdbFile(String pdfID) throws MalformedURLException, IOException {

        pdbID = pdfID;

        String url = uRL + pdfID + ".pdb";

        URL oracle = new URL(url);

        String fileaddress = fileAddress + "\\" + pdfID + ".txt";

        try (Writer writerPdb = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileaddress), "utf-8"))) {

            writerPdb.write("");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(oracle.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                writerPdb.write(inputLine);
            }
        } catch (IOException ex) {
            System.out.print("can not write on your files");
        }
        return fileaddress;
    }

    /**
     * input:address of pdb file
     *
     * search on pdb file and find information of ligandType, ligandChain and
     * ligandId .
     *
     * @return list(pdbid-ligandtype-ligandchain-ligandid) .
     */
    public List protoeinInformation(String fileaddress) throws FileNotFoundException, IOException {

        List resuList = new ArrayList();
        BufferedReader in = new BufferedReader(
                new FileReader(fileaddress));

        String inputLine;
        String s = "SITE_DESCRIPTION: BINDING SITE FOR RESIDUE";
        int indexNumber = 0;
        inputLine = in.readLine();

        while (inputLine.indexOf(s, indexNumber) != -1 && inputLine.indexOf(s, indexNumber) > indexNumber) {

            if (inputLine.contains(s) == true) {

                String re = pdbID + " " + inputLine.substring(inputLine.indexOf(s, indexNumber) + 43, inputLine.indexOf("REMARK ", inputLine.indexOf(s, indexNumber)));
                indexNumber = inputLine.indexOf("REMARK ", inputLine.indexOf(s, indexNumber));
                resuList.add(re);
            }
        }

        return resuList;

    }
}
