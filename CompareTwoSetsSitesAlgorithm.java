package Pipeline;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author mkordi
 */
public class CompareTwoSetsSitesAlgorithm  {
    
    private final String cutOffFileAddress;
    
    private final String sTDFileAddress;
    
    private final String compareTwoSetsSitesAddress;
    
    
    public CompareTwoSetsSitesAlgorithm (String codeAddress, String sTDFileAddress, String cutOffFileAddress ) throws IOException {
        this.compareTwoSetsSitesAddress = codeAddress;
        this.sTDFileAddress =  sTDFileAddress;
        this.cutOffFileAddress = cutOffFileAddress;
    }

   /**
     * Run CompareTwoSetsSites.pl on two step. First write a parameter on text file(method writeonfile), second run command perl(runscript)
     * 
     * @return 
     */
    private void CompareTwoSetsSitesInformation(List<String> queryList, List<String> dBDList, String temporaryFolderAddress,String outputname) throws IOException {
        
        List<String> address = generateConfigurationFile (queryList, dBDList, temporaryFolderAddress,outputname);
        
        String outputaddress, queryListaddress, dBDListaddress;
        
        queryListaddress = address.get(0);
        
        dBDListaddress = address.get(1);
         
        outputaddress = address.get(2);

        
       // runscript(outputaddress,queryListaddress,dBDListaddress, temporaryFolderAddress);
        
    }

   /**
     * run CompareTwoSetsSites.pl as perl GenerateCavity.pl  parameter
     *For running we need two parameter, First is queryList and second is queryListaddress, third is dBDList and forth id dBDListaddress and fifth is  sTDFileAddress and sixth is cutOffFileAddress and last one is address of outpuitfile.
     * 
     * @return 
     */
    public void runscript(String queryListName ,String queryListAddress,String dBDListName, String dBDListaddress,  String outputname) throws IOException {
        
      //  String command = "perl " + compareTwoSetsSitesAddress + " " +
           //    queryListaddress +"   "+ temporaryFolderAddress + "  "  
            //    + dBDListaddress + "  " + temporaryFolderAddress 
             //   + "  " +sTDFileAddress + "  " + cutOffFileAddress + "  " + outputaddress;
    //  Writer writerqueryList = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputname), "utf-8"));
         String command = "perl " + compareTwoSetsSitesAddress + "  " +
               queryListName +"   "+ queryListAddress + "  "  
                + dBDListName + "  " + dBDListaddress 
                + "  " +sTDFileAddress + "  " + cutOffFileAddress + "  " + outputname;
        
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
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = null;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Command Failure");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
        
    }
    
    
     /**
     * make  .txt file that will be a input to run GenerateCavity.pl This file contains queryList and dBDList
     * 
     *
     * @return address of txt file that created by this method.
     */
    private  List<String> generateConfigurationFile (List<String> queryList, List<String> dBDList, String temporaryFolderAddress, String outputname) throws UnsupportedEncodingException, FileNotFoundException {
        
        List<String> result = new ArrayList();
        String outputaddress, queryListaddress, dBDListaddress;
       
        outputaddress = temporaryFolderAddress+"\\"+outputname+".txt";
        queryListaddress = "filequeryList.txt";
        dBDListaddress =  "filedBDList.txt";
        
       
        
         result.add(queryListaddress);
         result.add(dBDListaddress);
         result.add(outputaddress);
         
          queryListaddress = temporaryFolderAddress+"\\"+"filequeryList.txt";
        dBDListaddress =  temporaryFolderAddress+"\\"+"filedBDList.txt";
        
        try (Writer writerqueryList = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(queryListaddress), "utf-8"));
                Writer writerBDList = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dBDListaddress), "utf-8"));
                Writer writerBDList1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputaddress), "utf-8"))) {
            
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
