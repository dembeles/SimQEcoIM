/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package godfin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import papi.Constants;
import papi.EventSet;
import papi.Papi;

/**
 *
 * @author test
 */
class CProcess {

    static ArrayList<ArrayList<String>> Table_info = new ArrayList<ArrayList<String>>();
   // static long[][] dataevent;
    private static ArrayList<ArrayList<Long>> dataevent= new ArrayList<ArrayList<Long>>();
    private static ArrayList<ArrayList<Double>> measurep=new ArrayList<ArrayList<Double>>();
    static Rengine re=null;
    static REXP resultR=null;
    public static double Cqpower=0.0;
    public static boolean notlauch=true;
    
    
    static void restoreSheData(String text, String text0) throws FileNotFoundException, SQLException, IOException {
    Connection con= DBcon.getConn();
    Statement stmt = con.createStatement();
    ResultSet rs;
    BufferedReader reader=new BufferedReader(new FileReader(text));
    String qry;
    // Restauration Schema
     while ((qry = reader.readLine())!= null )
            {   ArrayList<String> e= new ArrayList<>();
                stmt.executeUpdate(qry);
                if(qry.toUpperCase().startsWith("CREATE")){
                e.add(
                        qry.substring ( 
                         ((qry.substring(
                       0,qry.indexOf("(")) 
                        ).trim()).lastIndexOf("TABLE")+5,                       
                                ((qry.substring(
                       0,qry.indexOf("(")) 
                        ).trim()).length() 
                        ).trim()
                );
                
                e.add(
                        ""+(qry.substring ( 
                         qry.indexOf("(")+1,                       
                         qry.lastIndexOf(")") 
                        ).trim()).split(",").length
                );
                System.out.println(e.get(0));
                System.out.println(e.get(1));
                Table_info.add(e);
                }}
            System.out.println("Schema Ok:");
    // Restauration Data
    //String path = text0;
    
    reader=new BufferedReader(new FileReader(text0));
      while ((qry = reader.readLine())!= null )
       { stmt.executeUpdate(qry);}
      System.out.println("data Ok ");
    
      for(int i=0;i<Table_info.size();i++){
       rs = stmt.executeQuery("SELECT count(*) from "+Table_info.get(i).get(0));
                if (rs.next()) {
                //System.out.println(rs.getInt(1));
                ArrayList<String> e= new ArrayList<>();
                e=Table_info.get(i);
                e.add(""+rs.getInt(1));
                Table_info.set(i, e);
                System.out.println(Table_info.get(i).get(2));
                }
      }
    }



    static ArrayList<ArrayList<String>> extrat() {
      return Table_info;
    }    

    static ArrayList<ResultSet> ProcessQuery(String text, int i) throws SQLException, FileNotFoundException, IOException {

        Connection con= DBcon.getConn();
        Statement stmt = con.createStatement();
        ArrayList<ResultSet> rs=new ArrayList<ResultSet>();
        ResultSet rq=null;
        Thread t=new Thread();
        ThreadFactory dft= Executors.defaultThreadFactory();
        if(i==0){
            if(MainFenMDB.app){
             if(notlauch){
                  t = dft.newThread(new WattsUpWorker()); 
                  WattsUpWorker.i=0;
                  WattsUpWorker.b=true; 
                  t.start();
                  notlauch=false;
               }
              WattsUpWorker.brun=true; 
              WattsUpWorker.tp=0; 
              Cqpower=0;  
                //papi
             if(MainFenMDB.estimed){
             Papi.init();
             EventSet evset = EventSet.create(Constants.PAPI_TOT_CYC, Constants.PAPI_TOT_INS,Constants.PAPI_L1_DCM,Constants.PAPI_L2_TCM, Constants.PAPI_L3_TCM);
             evset.start();
                    rs.add(stmt.executeQuery(text));
             evset.stop();
             long [] dt=evset.getCounters();
             ArrayList<Long> p=new ArrayList<>();
             p.add(dt[0]);
             p.add(dt[1]);
             p.add(dt[2]);
             p.add(dt[3]);
             p.add(dt[4]);
             dataevent.add(p);
             }
             else {
                 rs.add(stmt.executeQuery(text));
             }
              
            WattsUpWorker.brun=false; 
            ArrayList<Double> aa=new ArrayList<>();
            aa.add(Cqpower/WattsUpWorker.tp);
            aa.add(Cqpower);
            measurep.add(aa);
            }
            else {
                //Papi
             if(MainFenMDB.estimed){
             Papi.init();
             EventSet evset = EventSet.create(Constants.PAPI_TOT_CYC, Constants.PAPI_TOT_INS,Constants.PAPI_L1_DCM,Constants.PAPI_L2_TCM, Constants.PAPI_L3_TCM);
             evset.start();
             rs.add(stmt.executeQuery(text));
             evset.stop();
            // dataevent[0] = new long[evset.getCounters().length];
             long [] dt=evset.getCounters();
             ArrayList<Long> p=new ArrayList<>();
             p.add(dt[0]);
             System.out.println("ii:"+dt[1]);
             p.add(dt[1]);
             p.add(dt[2]);
             p.add(dt[3]);
             p.add(dt[4]);
             dataevent.add(p);
             }
             else {
               rs.add(stmt.executeQuery(text));
             }} 
           }
        else
        {  // Multiple Query Process

            
            File f=new File(text);
            BufferedReader  br = new BufferedReader(new FileReader(f));
            String st,queries="";
            String [] Qlist;
            while((st=br.readLine())!=null){
                queries=queries+" "+st;}
            Qlist=queries.split(";");
            int j=0;
            if(MainFenMDB.app){
                  if(notlauch){
                  t = dft.newThread(new WattsUpWorker()); 
                  WattsUpWorker.i=0;
                  WattsUpWorker.b=true; 
                  t.start();
                  notlauch=false;
                  }   
             if(MainFenMDB.estimed){
                 for (String Qlist1 : Qlist) {
                 WattsUpWorker.brun=true; 
                 WattsUpWorker.tp=0; 
                 Cqpower=0; 
                 Papi.init();
                 EventSet evset = EventSet.create(Constants.PAPI_TOT_CYC, Constants.PAPI_TOT_INS,Constants.PAPI_L1_DCM,Constants.PAPI_L2_TCM, Constants.PAPI_L3_TCM);
                 evset.start();
                              rs.add(stmt.executeQuery(Qlist1));
                 evset.stop();
                  WattsUpWorker.brun=false; 
                             long [] dt=evset.getCounters();
                             ArrayList<Long> p=new ArrayList<>();
                              p.add(dt[0]);
                              p.add(dt[1]);
                              p.add(dt[2]);
                              p.add(dt[3]);
                              p.add(dt[4]);
                              dataevent.add(p);

                  ArrayList<Double> aa=new ArrayList<>();
                  aa.add(Cqpower/WattsUpWorker.tp);
                  aa.add(Cqpower);
                  measurep.add(aa); }
                  }else {
                 
                 for (String Qlist1 : Qlist) {
                 WattsUpWorker.brun=true; 
                 WattsUpWorker.tp=0; 
                 Cqpower=0; 
                              rs.add(stmt.executeQuery(Qlist1));
                  WattsUpWorker.brun=false; 
                  ArrayList<Double> aa=new ArrayList<>();
                  aa.add(Cqpower/WattsUpWorker.tp);
                  aa.add(Cqpower);
                  measurep.add(aa); }
                      
                  }           
           } 
            else {
               if(MainFenMDB.estimed){
                    for (String Qlist1 : Qlist) {
                    Papi.init();
                    EventSet evset = EventSet.create(Constants.PAPI_TOT_CYC, Constants.PAPI_TOT_INS,Constants.PAPI_L1_DCM,Constants.PAPI_L2_TCM, Constants.PAPI_L3_TCM);
                   evset.start();
                   rs.add(stmt.executeQuery(Qlist1));
                   evset.stop();
             long [] dt=evset.getCounters();
             ArrayList<Long> p=new ArrayList<>();
             p.add(dt[0]);
             p.add(dt[1]);
             p.add(dt[2]);
             p.add(dt[3]);
             p.add(dt[4]);
             dataevent.add(p);
                 } }
               else {
                   for (String Qlist1 : Qlist) {
                   rs.add(stmt.executeQuery(Qlist1));
                   }
                   }       
            }
        }
        
        return rs;    
    }

    static ArrayList<ArrayList<Double>> getRealp() {
       return measurep;
    }

    static ArrayList<ArrayList<Long>> getqueryevent() {
    return dataevent;
    }

    static public  void initRHyr(int ml){
    re.eval("dataml4<- data.frame(Power=c(40.92 ,40.93 ,40.94 ,40.95 ,40.96 ,40.97 ,40.98 ,40.99 ,40.1 ,40.101 ,40.102 ,40.103 ,40.67 ,40.68 ,39.12 ,39.73 ,39.57 ,39.36 ,39.3 ,39.45 ,39.67 ,39.49 ,39.82 ,39.83 ,39.07 ,39.64 ,39.65 ,39.49 ,39.50 ,39.65 ,39.61 ,39.19 ,39.07 ,39.58 ,39.43 ,39.43 ,39.44 ,39.93 ,39.47 ,39.59 ,39.77 ,39.62 ,39.64 ,39.5 ,39.6 ,39.64 ,39.83 ,39.29 ,38.88 ,39.24 ,39.54 ,39.16 ,39.32 ,39.33 ,39.52 ,39.44 ,39.3 ,39.61 ,39.39 ,39.23 ,39.35 ,39.36 ,39.55 ,39.26 ,39.35 ,39.29 ,39.24 ,39.14 ,39.79 ,39.24 ,39.56 ,39.33 ,39.52 ,39.27 ,39.17 ,39.41 ,39.37 ,39.39 ,39.47 ,39.48 ,39.43 ,39.69 ,39.4 ,39.27 ,39.4 ,39.41 ,39.49 ,39.9 ,39.44 ,39.47 ,39.33 ,39.39 ,39.29 ,39.41 ,39.27 ,39.15 ,39.32 ,39.27 ,39.3 ,39.67 ,39.87 ,39.88 ,39.7 ,39.71 ,39.94 ,39.4 ,39.19 ,39.37 ,31.38 ,39.28 ,39.57 ,39.35 ,39.36 ,38.21 ,30.47 ,39.35 ,39.56 ,39.63 ,39.65 ,39.69 ,39.26 ,39.34 ,39.29 ,39.39 ,39.34 ,39.28 ,39.28 ,39.26 ,39.31 ,39.36 ,39.88 ,39.89 ,39.38 ,39.39 ,39.66 ,39.52 ,39.44 ,39.55 ,39.62 ,39.56 ,39.4 ,39.67 ,39.66 ,39.65 ,39.47 ,39.44 ,39.6 ,39.41 ,39.41 ,39.38 ,39.37 ,39.64 ,39.47 ,39.48 ,39.33 ,39.05 ,39.73 ,39.38 ,39.46 ,39.29 ,39.74 ,39.6 ,39.23 ,39.52 ,68 ,66.93 ,55.96 ,62 ,57.1 ,55.8 ,53.75 ,55.45 ,48.58 ,50.8 ,50 ,47.2 ,49.6 ,47.25 ,44.85 ,48.9 ,41 ,45.85 ,42.1 ,47.1 ,44.45 ,45.05 ,40.33 ,49.45 ,41 ,49.2 ,58.86 ,61.93 ,46.35 ,48.4 ,39.1 ,42.1 ,44.6 ,48.4 ,61.2 ,50.8 ,47.2 ,55 ,39.5 ,43.8 ,54.75 ,41 ,42.5 ,45.1 ,47.25 ,41 ,44.55 ,40.2 ,42.7 ,45.12 ,43.06 ,45.13 ,43.12 ,45.26 ,47 ,45.4 ,42.5 ,45.35 ,42.18 ,44.72 ,42.7 ,41.5 ,42.1 ,46.65 ,43.1 ,44.6 ,45.5 ,40.03 ,40.8 ,44.32 ,43.6 ,44.04 ,46.5 ,41.26 ,48.1 ,40.52 ,41.65 ,44.32 ,42.25 ,41.26 ,41.66 ,42.78 ,45.4 ,40.84 ,44.32 ,40.9 ,39.94 ,40.22 ,45.65 ,39 ,43 ,51 ,41 ,41 ,41 ,43 ,46 ,42 ,41),\n" +
"		                    CPU=c(1430927 ,1430825 ,1154616 ,1154104 ,1153641 ,878422 ,877890 ,877800 ,128009 ,127827 ,127582 ,76154 ,76020 ,75963 ,75830 ,75749 ,75638 ,70575 ,70521 ,70418 ,60745 ,60412 ,59818 ,59772 ,59391 ,49578 ,49037 ,49020 ,48903 ,48825 ,48537 ,46236 ,46215 ,45731 ,38383 ,38288 ,38176 ,38053 ,37932 ,37861 ,28119 ,27964 ,23294 ,23060 ,22693 ,22686 ,22656 ,18596 ,18310 ,18284 ,18219 ,18184 ,18158 ,17941 ,17887 ,15312 ,15303 ,15293 ,15273 ,15111 ,15094 ,15002 ,14893 ,14847 ,14824 ,14778 ,14778 ,14632 ,14611 ,14591 ,12881 ,12775 ,12245 ,11980 ,11831 ,11801 ,11781 ,11730 ,11520 ,11462 ,11462 ,11306 ,11107 ,10848 ,10759 ,10753 ,10616 ,10012 ,9643 ,9634 ,9570 ,9558 ,9496 ,9492 ,9479 ,9472 ,9466 ,9258 ,9222 ,9167 ,9045 ,8931 ,8915 ,8898 ,8883 ,8735 ,8639 ,8548 ,8440 ,8348 ,8319 ,8314 ,8286 ,8274 ,8144 ,8136 ,8087 ,8016 ,7990 ,7976 ,7944 ,7925 ,7834 ,7762 ,7747 ,7690 ,7656 ,7530 ,6877 ,6858 ,6747 ,6716 ,6695 ,6676 ,6571 ,6406 ,6369 ,6363 ,6357 ,6357 ,6357 ,6345 ,6345 ,6345 ,6345 ,6333 ,6330 ,6315 ,6300 ,6300 ,6278 ,6181 ,6164 ,6134 ,6131 ,6125 ,6097 ,6067 ,6060 ,6058 ,6036 ,6030 ,6030 ,6014 ,151206960423 ,144562684780 ,140869417284 ,130510929895 ,129066862516 ,128703099961 ,127394841083 ,127042747365 ,125776837488 ,125612302541 ,123836072102 ,123645935747 ,123329223862 ,122826911123 ,122247549971 ,121983348125 ,121976303450 ,121751320258 ,121400865259 ,121327400430 ,121159794590 ,121104237048 ,121036485958 ,120927138761 ,120916801613 ,120910309554 ,120824264607 ,120818668783 ,120748565109 ,120737733138 ,120594247276 ,120502714953 ,120482751053 ,120481277491 ,120448281830 ,120271523812 ,120086281049 ,120064445805 ,120059693171 ,120055566950 ,120044587202 ,120038144007 ,120004367153 ,119997870815 ,119997027670 ,119997230887 ,119981916646 ,119980372447 ,119732521354 ,63695923398 ,63320472160 ,63025871994 ,62755677888 ,62378017770 ,62141832998 ,61901728090 ,61568712547 ,61464763367 ,61285483487 ,61274823840 ,60810977851 ,60809088319 ,60803256261 ,60779556142 ,60739102530 ,60737315483 ,60734468280 ,60723719968 ,60708977449 ,46554142604 ,46433016033 ,45672174050 ,45254403409 ,43797650894 ,43617569235 ,43561802772 ,43444505343 ,43237566758 ,43195508780 ,43121298249 ,43020036982 ,42953304284 ,42721916338 ,42671195919 ,42628529121 ,42496454919 ,42459160553 ,42444059704 ,42423924049 ,119935942859 ,119924129222 ,119914916347 ,119908091758 ,119859916741 ,119852012034 ,119851310358 ,119839346123 ,119826999757 ,119777875280),\n" +
"				    L2D=c(20558 ,22331 ,18150 ,17421 ,15495 ,13553 ,12577 ,11277 ,4509 ,4644 ,4513 ,4243 ,3837 ,3931 ,4556 ,4070 ,4345 ,3186 ,3091 ,3365 ,3629 ,3683 ,3657 ,3512 ,3502 ,3554 ,3199 ,3250 ,3218 ,3433 ,3417 ,3819 ,3500 ,3464 ,3151 ,3079 ,3126 ,3234 ,3086 ,3236 ,3070 ,3108 ,3331 ,3043 ,3061 ,2866 ,3212 ,3035 ,3334 ,3208 ,3067 ,3315 ,3266 ,2977 ,3171 ,3343 ,3279 ,3141 ,3102 ,3239 ,3129 ,3446 ,3262 ,3485 ,3382 ,3264 ,3169 ,3326 ,3280 ,3081 ,3156 ,3372 ,3058 ,3267 ,3226 ,3250 ,3325 ,3213 ,3260 ,3227 ,3349 ,3047 ,3053 ,2987 ,3065 ,3204 ,3248 ,3246 ,3208 ,3077 ,3164 ,3054 ,3015 ,3013 ,3042 ,3155 ,3069 ,3421 ,3376 ,3204 ,3265 ,3046 ,3133 ,3138 ,3198 ,3204 ,3225 ,3036 ,3074 ,3147 ,3054 ,3101 ,3246 ,3032 ,3123 ,3043 ,3127 ,2926 ,2886 ,3017 ,3251 ,3211 ,3266 ,3205 ,3156 ,3051 ,3134 ,2995 ,2992 ,3226 ,3134 ,3030 ,3113 ,3201 ,3092 ,3201 ,3041 ,2968 ,2990 ,3000 ,2957 ,3018 ,2816 ,2970 ,3094 ,3137 ,2961 ,3098 ,3006 ,3079 ,3339 ,3002 ,2907 ,2964 ,2961 ,3273 ,2886 ,2780 ,3067 ,2788 ,2864 ,2946 ,3186 ,3032 ,1372996989 ,1340758976 ,1324578669 ,1246435307 ,1297711662 ,1239767417 ,1317422737 ,1295135216 ,1270815455 ,1236201405 ,1306522839 ,1259787773 ,1296808201 ,1263417898 ,1259145679 ,1255065772 ,1238322348 ,1252066720 ,1238933055 ,1257268311 ,1253661736 ,1245575363 ,1250638312 ,1252057615 ,1250243824 ,1248502173 ,1236206391 ,1234766362 ,1248333401 ,1243800321 ,1232959600 ,1233352990 ,1242812357 ,1244059967 ,1232689782 ,1232772730 ,1233455071 ,1231658149 ,1239634695 ,1233874168 ,1233825060 ,1232927973 ,1238605362 ,1235910601 ,1238310294 ,1238565871 ,1238350314 ,1238230018 ,1230103952 ,683163887 ,655085126 ,651314635 ,648378999 ,640772944 ,652872965 ,638535912 ,639984056 ,639919402 ,636146757 ,636302990 ,631174991 ,631006955 ,631014263 ,630747140 ,629572720 ,629635829 ,629285101 ,629387945 ,629247273 ,445243110 ,431060263 ,440787366 ,440145279 ,427643122 ,435548350 ,427086692 ,431353122 ,437174849 ,430032940 ,429980142 ,428249086 ,426134798 ,426175731 ,427084433 ,427681506 ,426981214 ,426239399 ,426052361 ,428529993 ,1231247609 ,1231421232 ,1231616790 ,1230977065 ,1235116145 ,1234953247 ,1235400681 ,1235176778 ,1231736989 ,1229483586),\n" +
"				    L3D=c(42360 ,46051 ,34953 ,36003 ,33375 ,26147 ,26170 ,22872 ,8508 ,8724 ,8813 ,8349 ,7495 ,7744 ,8963 ,8156 ,8540 ,6161 ,6146 ,6500 ,6932 ,7007 ,6753 ,6731 ,6686 ,6796 ,6390 ,6574 ,6417 ,6662 ,6608 ,7225 ,6770 ,6804 ,6212 ,6117 ,6098 ,6234 ,6116 ,6260 ,6031 ,6064 ,6321 ,6052 ,6001 ,5643 ,6190 ,5902 ,6411 ,6206 ,5803 ,6161 ,6301 ,5863 ,6230 ,6574 ,6259 ,5937 ,6043 ,6236 ,6053 ,6609 ,6329 ,6770 ,6656 ,6343 ,6068 ,6505 ,6475 ,6181 ,6203 ,6505 ,6040 ,6278 ,6268 ,6265 ,6452 ,6273 ,6237 ,6174 ,6368 ,6012 ,6234 ,6072 ,6129 ,6118 ,6178 ,6352 ,6118 ,6069 ,6214 ,6002 ,5910 ,5946 ,6010 ,6074 ,5988 ,6653 ,6547 ,6203 ,6422 ,5930 ,6118 ,6036 ,6139 ,6191 ,6256 ,5903 ,6107 ,6012 ,6034 ,6025 ,6414 ,5975 ,6144 ,5989 ,6166 ,5833 ,5652 ,5972 ,6219 ,6055 ,6181 ,6085 ,6177 ,5774 ,6185 ,5834 ,5763 ,6015 ,6071 ,5969 ,6140 ,6105 ,5933 ,6298 ,5952 ,6001 ,5826 ,5884 ,5816 ,5915 ,5588 ,5841 ,6129 ,6116 ,5888 ,6013 ,5833 ,5995 ,6169 ,5853 ,5799 ,5826 ,5705 ,6076 ,5762 ,5537 ,6087 ,5540 ,5612 ,5794 ,6146 ,6096 ,5516850 ,5485850 ,5142900 ,6191250 ,6232100 ,7718150 ,7980000 ,6896350 ,4818900 ,6721750 ,6080450 ,4131100 ,5044800 ,6009550 ,4918550 ,5396100 ,4843350 ,7723900 ,5636850 ,5248950 ,4864450 ,5305850 ,4956100 ,6488050 ,5283850 ,4773500 ,8188250 ,6612500 ,6166950 ,6177000 ,7122250 ,4247750 ,6018350 ,5632900 ,5730050 ,5884900 ,6818000 ,5008400 ,4663450 ,5080650 ,5403250 ,4763000 ,6195850 ,4582500 ,5777400 ,5046200 ,5421600 ,6645250 ,5364250 ,6415100 ,5198700 ,4735700 ,5215850 ,5053450 ,5687950 ,9546050 ,4735188 ,5430250 ,5024200 ,4814400 ,4886250 ,5359050 ,5543550 ,4138300 ,5296100 ,4141150 ,4280300 ,5193650 ,4369150 ,6756500 ,5453300 ,3756200 ,6127400 ,5270650 ,5059250 ,9951650 ,5171200 ,5864850 ,5938750 ,5259100 ,5317800 ,5946550 ,5261350 ,5016050 ,6081350 ,4043600 ,5027350 ,5816500 ,4887750 ,4448300 ,4806200 ,5374550 ,5835100 ,5145050 ,5023550 ,6494050 ,6416700 ,4257300 ,4486100),\n" +
"                                   mem=c(13407 ,13867 ,12071 ,11656 ,12323 ,10338 ,10237 ,10396 ,5635 ,5456 ,5896 ,5618 ,5835 ,5836 ,6103 ,5744 ,5580 ,5009 ,4822 ,4974 ,5331 ,5501 ,5476 ,5475 ,5180 ,4877 ,5236 ,5278 ,5249 ,5209 ,5271 ,5336 ,5066 ,5626 ,4755 ,5102 ,4930 ,5122 ,4841 ,4788 ,4652 ,4544 ,4743 ,4957 ,4806 ,4414 ,4656 ,4862 ,4848 ,4721 ,4435 ,4730 ,5035 ,4523 ,4640 ,5194 ,4687 ,4426 ,4552 ,5003 ,5026 ,5176 ,5197 ,5031 ,5053 ,4752 ,4557 ,4811 ,5247 ,5060 ,4563 ,4815 ,4521 ,4917 ,4773 ,4742 ,4722 ,4755 ,4762 ,4607 ,4985 ,5077 ,4944 ,5046 ,4803 ,4791 ,5118 ,5120 ,4686 ,4546 ,4624 ,4419 ,4789 ,4553 ,4476 ,4803 ,4595 ,5248 ,5212 ,4828 ,5235 ,4490 ,4944 ,5002 ,4894 ,5142 ,5166 ,4828 ,4984 ,4597 ,4524 ,5097 ,5208 ,5037 ,5032 ,4476 ,5052 ,4469 ,4765 ,4931 ,5059 ,4552 ,4484 ,4774 ,4952 ,4419 ,4666 ,4745 ,4493 ,4472 ,4996 ,4914 ,5071 ,5002 ,4637 ,5097 ,5022 ,5010 ,4936 ,4984 ,4745 ,4816 ,4636 ,4914 ,5127 ,4881 ,4979 ,5010 ,4903 ,4921 ,4734 ,4443 ,4681 ,4623 ,4332 ,4408 ,4643 ,4573 ,4613 ,4612 ,4600 ,4744 ,5065 ,4759 ,651965255 ,630515380 ,637793675 ,621419890 ,626536768 ,606153961 ,654791739 ,635024633 ,616451101 ,617738590 ,635610330 ,617670789 ,644072709 ,617226935 ,616473763 ,609385149 ,610369609 ,612719640 ,605051306 ,619286488 ,625095899 ,619085145 ,606452881 ,602845149 ,613532228 ,618796588 ,607860705 ,617322501 ,611047063 ,603560912 ,607735681 ,607230602 ,608047290 ,600517505 ,610803236 ,613834503 ,594147712 ,609149039 ,617476112 ,615922055 ,605352782 ,607448362 ,598244727 ,606866684 ,602697350 ,613738210 ,598277633 ,606183975 ,607310957 ,300039172 ,291741751 ,292037027 ,289884705 ,286048249 ,296641827 ,290412911 ,290551580 ,288420417 ,285907750 ,283099236 ,281971459 ,279495397 ,281589178 ,283679386 ,283975091 ,285365300 ,282412178 ,281264457 ,284713513 ,184895898 ,180115114 ,184772092 ,182166886 ,177310132 ,178033053 ,173746800 ,176428123 ,177720614 ,179477749 ,178586187 ,177967116 ,175725378 ,176047093 ,177557117 ,177747467 ,178160405 ,178694533 ,179010688 ,175256096 ,610514875 ,608465163 ,614245167 ,608541527 ,613558730 ,613227737 ,605736567 ,602595168 ,604681586 ,611753616)\n" +
");");
    
     switch (ml) {
         case 1:
    re.eval("hyriml01<-lm(Power ~ poly(CPU, L2D, L3D, mem, degree = 2, raw = TRUE), \n" +
"    data = dataml4)");
    break;
    
          case 2:
    re.eval("hyriml02<-neuralnet(Power ~ CPU + L2D + L3D + mem, data = dataml4, \n" +
"    hidden = c(8), threshold = 0.08, stepmax = 50000, rep = 40, \n" +
"    learningrate.limit = NULL, learningrate.factor = list(minus = 0.5, \n" +
"        plus = 1.2), lifesign = \"minimal\", algorithm = \"rprop+\", \n" +
"    act.fct = \"logistic\", linear.output = TRUE)");
      break;
    
          default:
    re.eval("hyriml03<-train(Power ~ ., data = dataml4, method = \"rf\", \n" +
"    trControl = trControl, importance = TRUE, nodesize = 14, \n" +
"    ntree = 500)");   
      break;
     }
}
    static void InitRlearning() {

    if (!Rengine.versionCheck()) {
    System.err.println("Java version mismatch.");
    System.exit(1);
    }
    String my[] = {"--vanilla"};
    re=new Rengine(my,false,new TextConsole());
    if (!re.waitForR()) {
    System.out.println("Cannot load R");
    System.exit(1);
    }
    REXP result = re.eval("7+10");
    System.out.println("rexp: " + (result.asDouble()+3.0));
    
    //   initialisation
    re.eval("library(neuralnet)");
    re.eval("library(caret)");
    re.eval("library(randomForest)");
    re.eval("library(ISLR)");
    re.eval("library(e1071)");
    re.eval("library(tree)");
    re.eval("trControl <- trainControl(method = \"cv\", number = 10, search = \"grid\")");
    System.out.println("init ok");
    //
    
    }
    
    static void runR(String frameR, int i) {
    
        resultR = re.eval(frameR);
        System.out.println("eval ok");
        switch (i) {
            case 1:
                resultR=re.eval("predict.lm(hyriml01, newdata=t)");
                //resultR=re.eval("predict.lm(postml21, newdata=t)");
                break;
            case 2:
                resultR=re.eval("predict(hyriml02, newdata=t)");
                //resultR=re.eval("predict(postml22, newdata=t)");
                break;
            default:
                resultR=re.eval("predict(hyriml03, newdata=t)");
                //resultR=re.eval("predict(postml22, newdata=t)");
                break;
        }
    }
    
    static public  REXP getRoutput() {
      return resultR;
    }

    

}
