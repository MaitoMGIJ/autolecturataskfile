/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ayesa.autolecturataskfile;

import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author felipe
 */
public class InputInterface {

    public static void onManualInterface(String environment, String processDate, String subsidiary, String cycles) {
        try {
            TaskFile tf = new TaskFile(environment);

            NewCycles nc = new NewCycles();
            nc.setProcessDate(processDate);
            nc.setSubsidiary(subsidiary);
            nc.setCycles(cycles);
            nc.setRows("0");
            tf.onGenerateReportFile(nc);

        } catch (SQLException | IOException | ClassNotFoundException ex ) {
            System.out.println("5### HA OCURRIDO UN ERROR: " + ex.getMessage() + ex.getStackTrace());
        }
    }

    public static void onAutomaticInterface(String environment) {
        try {
            TaskFile tf = new TaskFile(environment);
            tf.onGenerateReportFile(null);

        } catch (SQLException | IOException | ClassNotFoundException ex) {
            System.out.println("6### HA OCURRIDO UN ERROR: " + ex.getMessage() + ex.getStackTrace());
        }
    }

    public static void main(String[] args) {
        
        /*String environment = "PRE";
        String processDate = "01/08/2019";
        String subsidiary = "6000";
        String sector = "29";
        
        onManualInterface(environment, processDate, subsidiary, sector);*/

        if (args.length == 4) {
            String environment = args[0];
            String processDate = args[1];
            String subsidiary = args[2];
            String cycles = args[3];

            if (subsidiary.equals("0000")) {
                onAutomaticInterface(environment);
            } else {
                onManualInterface(environment, processDate, subsidiary, cycles);
            }
        } else {
            System.out.println("SE REQUIEREN 4 PARAMETROS, SE INGRESARON " + args.length);
        }
    }

}
