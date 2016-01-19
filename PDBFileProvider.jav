package Pipeline;

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
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author mkordi
 */
public class PDBFileProvider {

    //shows  PDB website url
    private String PDBUpStreamUrl = "http://www.rcsb.org/pdb/files/";

    private String pdbID = null;

    public PDBFileProvider() {

    }

    public PDBFileProvider(String URL) {
        this.PDBUpStreamUrl = URL;
    }

    /**
     * input:pdfID, Id of protein and fileaddress address of file that write
     * data on it
     *
     * create file on fileAddress folder with namr id.txt and get onformation
     * from URL and write on txt file
     *
     * @return address of txt file that created by this method.
     */
    public String writePdbFile(String pdfiD, String fileAddress) throws MalformedURLException, IOException {

        pdbID = pdfiD;

        String fileAddress2 = fileAddress;

        String url = PDBUpStreamUrl + pdbID + ".pdb";

        URL urlAddress = new URL(url);

        fileAddress = fileAddress + "/" + pdbID + ".pdb";

        String url2 = PDBUpStreamUrl + pdbID + ".dssp";

        URL urlAddress2 = new URL(url2);

        fileAddress2 = fileAddress2 + "/" + pdbID + ".dssp";

        try (Writer writerPdb = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileAddress), "utf-8"));
                Writer writerPdb2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileAddress2), "utf-8"))) {

            writerPdb.write("");
            writerPdb2.write("");

            URL website = new URL(url);
            URLConnection connection = website.openConnection();
            BufferedReader in1 = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));

            String inputLine;

            while ((inputLine = in1.readLine()) != null) {
                writerPdb.write(inputLine + "\n");
            }
            in1.close();

            URL website2 = new URL(url2);
            URLConnection connection2 = website2.openConnection();
            BufferedReader in2 = new BufferedReader(
                    new InputStreamReader(
                            connection2.getInputStream()));

            while ((inputLine = in2.readLine()) != null) {
                writerPdb2.write(inputLine + "\n");
            }
            in2.close();

        } catch (IOException ex) {
            System.err.println("Exception Message1: Can not connect to this URL Address, Please check your address and try again" + "\n ");
        }
        return fileAddress;
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

        List resultList = new ArrayList();

        String inputLine;
        String s = "SITE_DESCRIPTION: BINDING SITE FOR RESIDUE", s1 = "REMARK";
        int indexNumber = 0;
        try {
            BufferedReader in = new BufferedReader(
                    new FileReader(fileaddress));
            inputLine = in.readLine();
            while ((inputLine.indexOf(s, indexNumber) != -1)) {

                if (inputLine.contains(s) == true) {

                    if (inputLine.indexOf(s1, inputLine.indexOf(s, indexNumber)) == -1) {
                        s1 = "DBREF";
                    }
                    String re = pdbID + " " + inputLine.substring(inputLine.indexOf(s, indexNumber) + s.length(), inputLine.indexOf(s1, inputLine.indexOf(s, indexNumber)));

                    if (inputLine.indexOf(s1, inputLine.indexOf(s, indexNumber)) != -1) {
                        indexNumber = inputLine.indexOf(s1, inputLine.indexOf(s, indexNumber));
                    } else {

                        s1 = "DBREF";
                        indexNumber = inputLine.indexOf(s1, inputLine.indexOf(s, indexNumber));
                    }

                    resultList.add(re);
                }
            }
            if (inputLine.contains(s) == false) {
                System.err.println("Exception Message2: There is not any information for this PdbID, Please check PdbId and try again");
            }
        } catch (IOException e) {
            System.err.println("Can not read from your PDB file");
        }
        return resultList;

    }
}
