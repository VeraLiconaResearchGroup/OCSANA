/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pipeline;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

/**
 *
 * @author mkordi
 */
public class Preparedata {

    String Address;

    public Preparedata(String inputAddress) {
        this.Address = inputAddress;
    }

    public String[] prepare() throws UnsupportedEncodingException, IOException {

        // The name of the file to open.
        String[] result = new String[2];
        PDBFileProvider a = new PDBFileProvider();
        String fileName = this.Address;
      //  System.out.println(fileName);

        // This will reference one line at a time
        String line = null;
        String temp1 = null;
         String temp2 = null;
        int atemp = 0;

        Path currentRelativePath = Paths.get("");
        String currectpath = currentRelativePath.toAbsolutePath().toString();
      //  System.out.println("Current relative path is: " + currectpath);
        File f = File.createTempFile("tmp", ".txt", new File(currectpath));
         File f2 = File.createTempFile("tmp", ".txt", new File(currectpath));
        String tempfileAddress = f.getAbsolutePath();
        String tempfileAddress2 = f2.getAbsolutePath();
        result[0] = tempfileAddress;
        result[1] = tempfileAddress2;
        
    //    System.out.println(tempfileAddress);
       //  System.out.println(tempfileAddress2);

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader
                    = new FileReader(fileName);

            //  wrap FileReader in BufferedReader.
            BufferedReader bufferedReader
                    = new BufferedReader(fileReader);

            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(f2));

            while ((line = bufferedReader.readLine()) != null) {
            //    System.out.println(line);

                StringTokenizer st = new StringTokenizer(line);

                temp1 = null;
                atemp = 0;
                while (st.hasMoreTokens()) {

                    String name = st.nextToken();
                 //   System.out.println(name);
                    if (atemp == 0) {
                        temp1 = name;
                         temp2 = name;
                        a.writePdbFile(name, currectpath);
                    } else {
                        temp1 = temp1 + "_" + name;
                         temp2 = temp2 + "\t" + name;
                        break;
                    }
                    atemp++;

                    //break;
                }
               // System.out.println(temp1);
              
                bw.write(temp1 + "\n");
                 bw2.write(temp2 + "\n");

            }
bw.close();
bw2.close();
            // Always close files.
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '"
                    + fileName + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                    + fileName + "'");

        }
       
        return result;
    }

}
