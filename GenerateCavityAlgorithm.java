package Pipeline;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 *
 * @author mkordi
 */
public class GenerateCavityAlgorithm {

    //address of GenerateCavityFile.pl file
    private final String GenerateCavityFileAddress;

    public GenerateCavityAlgorithm(String codeAddress) {

        GenerateCavityFileAddress = codeAddress;
    }

    /**
     * Run GenerateCavity.pl on two step. First write a parameter on text
     * file(method writeonfile), second run command perl(runscript)
     *
     * @return
     */
    public void generateCavityInformation(String fileAddress, String cutOff) throws IOException {

      //  String address = generateConfigurationFile(pdbId, ligandType, ligandChain, ligandId);

        runscript(fileAddress, cutOff);

    }

    /**
     * make .txt file that will be input to runn GenerateCavity.pl This file
     * contains pdbId,ligandType, ligandChain, ligandId.
     *
     * @return address of txt file that created by this method.
     */
    private String generateConfigurationFile(String pdbId, String ligandType, String ligandChain, int ligandId) throws IOException {

        boolean empty = true;

       // String address = temporaryFolderAddress+"\\GenerateCavity"+pdbId+".txt";
        // File file = new File(address);
        File temp = File.createTempFile(pdbId, ".txt");
       // System.out.println(temp.getAbsolutePath());
        String address = temp.getAbsolutePath();

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(address), "utf-8"))) {

            writer.write("");
            writer.write(pdbId + "\t" + ligandType + "\t" + ligandChain + "\t" + Integer.toString(ligandId));
        } catch (IOException ex) {
            System.out.print("can not write on" + address);
        }

        return address;
    }

    /**
     * run GenerateCavity.pl as perl GenerateCavity.pl parameter For running we
     * need two parameter, First is address of txt file that created by
     * writeonfile method and second is cutOff.
     *
     * @return
     */
    private void runscript(String address, String cutOff) throws IOException {

       String command = "perl" + "  " + GenerateCavityFileAddress  +"  "+ address + "   " + cutOff;
      //   String command = "perl" + "  " + GenerateCavityPoint_Vectorize.pl  +"  "+ "text.txt" + "   " + cutOff;
      //  System.out.println(command);
        Process process;

        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
            if (process.exitValue() == 0) {
                System.out.println("Command Successful");
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = null;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Command Failure" + command);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }

    }
    

}
