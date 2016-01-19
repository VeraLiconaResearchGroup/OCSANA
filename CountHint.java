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
public class CountHint {

    //address of GenerateCavityFile.pl file
    private final String CountHintFileAddress;

    public CountHint(String codeAddress) {

        CountHintFileAddress = codeAddress;
    }

   

    /**
     * run GenerateCavity.pl as perl GenerateCavity.pl parameter For running we
     * need two parameter, First is address of txt file that created by
     * writeonfile method and second is cutOff.
     *
     * @return
     */
    public void runscriptString(String QueryPointList , String SiteComparisonResult , String ScoreCutoff ) throws IOException {

       String command = "perl" + "  " + CountHintFileAddress  +"  "+ QueryPointList + "   " + SiteComparisonResult + "  " +  ScoreCutoff ;
      //   String command = "perl" + "  " + GenerateCavityPoint_Vectorize.pl  +"  "+ "text.txt" + "   " + cutOff;
       // System.out.println(command);
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
            if (process.exitValue() == 0) {
               // System.out.println("Command Successful");
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
                 try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = null;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(" Command Failure: \t" + "Illegal division by zero at CountHit.pl line 39.");
            }
        } catch (Exception e) {
            
            System.out.println("Exception: " + e.toString());
        }

    }
    

}
