/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package godfin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 *
 * @author test
 */
public class GodFin {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
       /* 
    File sub1[] = null;
    File f_read;
        File fstream;
        FileWriter out;
        PrintWriter pw = null;
        BufferedReader br;
        String st; 
        File dir;

      String pathq = "/home/test/Desktop/test/hry/q/H";  
        dir = new File(pathq); 
       if(dir.exists() && dir.isDirectory()) 
         { 
            sub1 = dir.listFiles();                   
            Arrays.sort(sub1); 
         }
       int j=1;
   for(int i=0; i<sub1.length; i++){ 
        fstream = new File(sub1[0].getParentFile().getPath()+"/PostgresL.txt");
        if(fstream.createNewFile()){
                    out = new FileWriter(fstream);
                    pw = new PrintWriter(out);
                    pw.println("Item;Query;Instruction_cycle;Instruction_executed;L3_miss;Ave_power;Energy;Min;Max;IO;CPU;C_READ;C_HIT;time");
                    pw.close();  }  
           out = new FileWriter(fstream, true);
           pw = new PrintWriter(out);

             f_read= new File(sub1[i].getPath());
             br = new BufferedReader(new FileReader(f_read));
             br.readLine();
             while((st=br.readLine())!=null){
                 pw.print(j+";");
                 pw.println(st);
                 j++;
              }
              br.close();
              pw.close();
    }
*/
        MainFenMDB fen=new MainFenMDB();
       //fen.setFocusableWindowState(false);
     fen.pack();
     fen.setVisible(true);
       
    }
    
}
