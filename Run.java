package Pipeline;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Run {

    public static void main(String args[]) throws IOException {

        String scriptsAddress = args[0];
        String input1Address = args[1];
        String DBD_ListName = args[2];
        String DBD_ListAddress;
        String cutOff = "4A";
        String[] Add =  new String[2];
        

        Preparedata pre = new Preparedata(input1Address);
        Add =  pre.prepare();
        String tempFileAddress = Add[0];
        String tempFileAddressCount = Add[1];

        GenerateCavityAlgorithm a = new GenerateCavityAlgorithm(scriptsAddress + "GenerateCavityPoint_Vectorize.pl");
        a.generateCavityInformation(input1Address, cutOff);

        CompareTwoSetsSitesAlgorithm b = new CompareTwoSetsSitesAlgorithm(scriptsAddress + "CompareTwoSetsSites.pl ", scriptsAddress + "ParameterFiles/All1160Cavity.std", scriptsAddress + "ParameterFiles/TcCutoff4Normalize.txt");

        Path currentRelativePath = Paths.get("");
        String currectpath = currentRelativePath.toAbsolutePath().toString();
        File outputAddress = File.createTempFile("out", ".txt", new File(currectpath));
        String outputTempFileAddress = outputAddress.getAbsolutePath();
        
        
        String tempFileName = tempFileAddress;
        tempFileAddress = currectpath+"/";
        
          DBD_ListAddress = scriptsAddress + "ParameterFiles/DrugBindingVectors/";
          
        b.runscript(tempFileName, tempFileAddress,DBD_ListName, DBD_ListAddress,  outputTempFileAddress);

        
        CountHint c = new CountHint(scriptsAddress + "CountHit.pl ");
        
       
        c.runscriptString(tempFileAddressCount, outputTempFileAddress, "-2");
     
    }
}
