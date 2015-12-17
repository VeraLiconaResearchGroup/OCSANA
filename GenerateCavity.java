package org.cytoscape.myapp.internal.Drugeability;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 *
 * @author mkordi
 */
public class GenerateCavity {
    
    private final String GenerateCavityFileAddress;

    public GenerateCavity(String codeAddress) {
        
        GenerateCavityFileAddress = codeAddress;
    }

    
    /**
     * Run GenerateCavity.pl on two step. First write a parameter on text file(method writeonfile), second run command perl(runscript)
     * 
     * @return 
     */
    public void runAlgorithm(String pdbId, String ligandType, String ligandChain, int ligandId, double cutOff, String temporaryFolderAddress ) throws IOException{
        
        String address = writeonfile(pdbId, ligandType, ligandChain, ligandId, temporaryFolderAddress);
       
        runscript(address, cutOff);
        
    }
    
    /**
     * make  .txt file that will be input to runn GenerateCavity.pl This file contains pdbId,ligandType, ligandChain,
     * ligandId.
     *
     * @return address of txt file that created by this method.
     */
    private String writeonfile(String pdbId, String ligandType, String ligandChain, int ligandId, String temporaryFolderAddress) throws IOException {

	File file; 
        int empty = 0 ; 
        
        String address = "temporaryFolderAddress:\\GenerateCavity"+pdbId+".txt";
        
        file = new File(address);
        if (!file.createNewFile()){
	      empty = 1;
	}
        
        try(Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("GenerateCavity."+pdbId+".txt"), "utf-8"))) {
            
        if (empty == 1 )
             writer.write("");
        writer.write(pdbId + "\t" + ligandType + "\t" +  ligandChain + "\t" +Integer.toString(ligandId));
        } catch (IOException ex) {
         System.out.print("can not write on GenerateCavity"+pdbId+".txt file"  );
        } 

    return address;
    }

    
    /**
     * run GenerateCavity.pl as perl GenerateCavity.pl  parameter
     *For running we need two parameter, First is address of txt file that created by writeonfile method and second is cutOff.
     * 
     * @return 
     */
    private void runscript(String address, double cutOff ) throws IOException {
        
        String command = "perl " + GenerateCavityFileAddress+ "\\GenerateCavity.pl "+ address + "  " + Double.toString(cutOff);
                
        Runtime.getRuntime().exec(command);

       
    }

}
