/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ayesa.autolecturataskfile.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import java.sql.*; 

/**
 *
 * @author felipe
 */
public class ConnectionFactory {
    
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;
    

    public ConnectionFactory(String environment) throws FileNotFoundException, IOException {
        
        InputStream input = new FileInputStream("resources/config.properties");
        Properties prop = new Properties();
        prop.load(input);

        if ( environment.equals("PRE") ) {
            DB_URL = prop.getProperty("PRE_DB_URL");
            DB_USER = prop.getProperty("PRE_DB_USER");
            DB_PASSWORD = prop.getProperty("PRE_DB_PASSWORD");
        }

        if ( environment.equals("PROD") ) {
            DB_URL = prop.getProperty("PROD_DB_URL");
            DB_USER = prop.getProperty("PROD_DB_USER");
            DB_PASSWORD = prop.getProperty("PROD_DB_PASSWORD");
        }
         
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException  {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

}
