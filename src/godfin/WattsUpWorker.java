/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package godfin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author postgres
 */
public class WattsUpWorker implements Runnable{
static boolean b=true, brun=false; 
static int i=0,  tp=0;
      public static Process p;
      ProcessBuilder pb = new ProcessBuilder();
       String line; 
      public static String Prtime;
       BufferedReader reader;
      public void run() {
              pb.command("bash", "-c", "sudo wattsup ttyUSB0 watts");
              try {
                  p=pb.start();
              } catch (IOException ex) {
                  Logger.getLogger(WattsUpWorker.class.getName()).log(Level.SEVERE, null, ex);
              }
    try {
         reader=new BufferedReader(new InputStreamReader(
         p.getInputStream()));
        // File fos= new File("/home/postgres/Desktop/test/"+i+"/"+"_Po.txt");
        // Writer writer=new FileWriter(fos);
 //        FileOutputStream fos = new FileOutputStream("/home/postgres/Desktop/test/"+i+"/"+"_Po.txt");
 //b=true;
 int l=0;        
 while((line = reader.readLine())!=null){       
         System.out.println("name="+Thread.currentThread().getName()+"value="+line);
        // writer.write(line+"\n");
        Prtime=line;
         if(brun) {tp++; CProcess.Cqpower+=Double.parseDouble(line);}
         if(b==false && l>=5){System.out.println("FIN"); break;}l++;
           }
         reader.close();
       //  writer.close();
           p.destroy();  } catch (IOException ex) {
           Logger.getLogger(WattsUpWorker.class.getName()).log(Level.SEVERE, null, ex);
         }
 }}
