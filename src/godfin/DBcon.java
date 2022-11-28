/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package godfin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * @author test
 */
public class DBcon {
    
static String url = "jdbc:monetdb://";
static Connection con=null;
//Default port=50000

//String url = "jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true";

 static Connection DBcon(String ur, String user, String pass, String db) throws SQLException, ClassNotFoundException{
    
         //Registering the HSQLDB JDBC driver
        // Class.forName("org.hsqldb.jdbc.JDBCDriver");
         //Creating the connection with HSQLDB
         if (db!=null) ur=ur+":"+db;
         con = DriverManager.getConnection(ur, user, pass);
     
     
    return con;
}

 static boolean checkConDB(String url, String user, String pass, String db) throws SQLException, ClassNotFoundException{
    if(DBcon(url, user, pass, db)!=null) return true;
    return false;
}
 static Connection getConn(){
    return con;
}
}
