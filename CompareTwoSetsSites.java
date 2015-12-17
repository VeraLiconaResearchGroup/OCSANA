package org.cytoscape.myapp.internal.Drugeability;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author mkordi
 */
public class CompareTwoSetsSites {

    private final String cutOffFileAddress;

    private final String sTDFileAddress;

    private final String compareTwoSetsSitesAddress;

    public CompareTwoSetsSites(String sTDFileAddress, String cutOffFileAddress, String codeAddress) throws IOException {
        this.compareTwoSetsSitesAddress = codeAddress;
        this.sTDFileAddress = sTDFileAddress;
        this.cutOffFileAddress = cutOffFileAddress;
    }

    /**
     * Run CompareTwoSetsSites.pl on two step. First write a parameter on text
     * file(method writeonfile), second run command perl(runscript)
     *
     * @return
     */
    public void runAlgorithm(List<String> queryList, List<String> dBDList, String temporaryFolderAddress) throws IOException {

        List<String> address = writeonfile(queryList, dBDList, temporaryFolderAddress);

        String outputaddress, queryListaddress, dBDListaddress;

        queryListaddress = address.get(0);

        dBDListaddress = address.get(1);

        outputaddress = address.get(2);

        runscript(outputaddress, queryListaddress, dBDListaddress, temporaryFolderAddress);

    }

    /**
     * run CompareTwoSetsSites.pl as perl GenerateCavity.pl parameter For
     * running we need two parameter, First is queryList and second is
     * queryListaddress, third is dBDList and forth id dBDListaddress and fifth
     * is sTDFileAddress and sixth is cutOffFileAddress and last one is address
     * of outpuitfile.
     *
     * @return
     */
    private void runscript(String outputaddress, String queryListaddress, String dBDListaddress, String temporaryFolderAddress) throws IOException {

        String command = "perl " + compareTwoSetsSitesAddress + "\\CompareTwoSetsSites.pl "
                + temporaryFolderAddress + "  " + queryListaddress
                + temporaryFolderAddress + "  " + temporaryFolderAddress + "\\ParameterFiles\\DrugBindingVectors"
                + " " + sTDFileAddress + " " + cutOffFileAddress + " " + outputaddress;

        Runtime.getRuntime().exec(command);

    }

    /**
     * make .txt file that will be a input to run GenerateCavity.pl This file
     * contains queryList and dBDList
     *
     *
     * @return address of txt file that created by this method.
     */
    private List<String> writeonfile(List<String> queryList, List<String> dBDList, String temporaryFolderAddress) throws UnsupportedEncodingException, FileNotFoundException {

        List<String> result = null;
        String outputaddress, queryListaddress, dBDListaddress;

        outputaddress = "temporaryFolderAddress\\results_pair_score.txt";
        queryListaddress = "temporaryFolderAddress\\filequeryList.txt";
        dBDListaddress = "temporaryFolderAddress\\filedBDList.txt";

        result.add(queryListaddress);
        result.add(dBDListaddress);
        result.add(outputaddress);

        try (Writer writerqueryList = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(queryListaddress), "utf-8"));
                Writer writerBDList = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dBDListaddress), "utf-8"))) {

            writerqueryList.write("");
            writerBDList.write("");

            for (int i = 0; i < queryList.size(); i++) {

                String string = queryList.get(i);
                writerqueryList.write(string);
            }

            for (int i = 0; i < dBDList.size(); i++) {

                String string = dBDList.get(i);
                writerBDList.write(string);
            }

        } catch (IOException ex) {
            System.out.print("can not write on your files");
        }
        return result;
    }

}
