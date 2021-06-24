/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ayesa.autolecturataskfile;

import com.ayesa.autolecturataskfile.dao.TaskFileDao;
import com.ayesa.autolecturataskfile.dao.TaskFileDaoImpl;
import com.ayesa.autolecturataskfile.db.ConnectionFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import java.sql.SQLException;

/**
 *
 * @author felipe
 */
public class TaskFile {

    private final TaskFileDao tf;

    private DateFormat fileDateFormat;
    private DateFormat dateFormat;

    public TaskFile(String environment) throws SQLException, IOException, ClassNotFoundException {
        ConnectionFactory conn = new ConnectionFactory(environment);
        tf = new TaskFileDaoImpl(conn.getConnection());
    }

    public void onGenerateReportFile(NewCycles mnc) {
        try {

            fileDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            System.out.println("------------------------------------------------");

            ControlParameters cp = tf.getControlParameters();
            System.out.println("### Separador: " + cp.getSeparator());
            System.out.println("### NombreArchivo: " + cp.getFileName());
            System.out.println("### SFTPServidor: " + cp.getSFTPServer());
            System.out.println("### SFTPUsuario: " + cp.getSFTPUser());
            System.out.println("### SFTPArchivoLlave: " + cp.getSFTPKeyFilePath());
            System.out.println("### SFTPRutaArchivo: " + cp.getSFTPFilePath());

            System.out.println("------------------------- Inicio Interface Autolecturas -----------------------");
            Date startDate = new Date();
            System.out.println("### Inicio de ejecución: " + dateFormat.format(startDate));

            if (mnc != null) {
                cp.setMode("Manual");
                onProcessCycle(cp, mnc);
            } else {
                cp.setMode("Automatico");
                List<NewCycles> lnc = tf.lookupNewCycles();

                for (NewCycles anc : lnc) {
                    onProcessCycle(cp, anc);
                }
            }

            Date endDate = new Date();
            System.out.println("### Fin de ejecución: " + dateFormat.format(endDate));
            System.out.println("Ok Finaliza el Proceso de Generación de Interfaces a las : " + dateFormat.format(endDate));
            System.out.println("-------------------------- Fin Interface Autolecturas -------------------------");

        } catch (Exception ex) {
            System.out.println("### HA OCURRIDO UN ERROR: " + ex.getMessage());
        }
    }

    public void onProcessCycle(ControlParameters cp, NewCycles nc) {
        try {
            System.out.println("##########################################################################");
            System.out.println("### Fecha proceso: " + nc.getProcessDate());
            System.out.println("### Fecha: " + nc.getFileDate());
            System.out.println("### Sucursal: " + nc.getSubsidiary());
            System.out.println("### Ciclos: " + nc.getCycles());
            System.out.println("### Registros: " + nc.getRows());

            long pe = tf.lookupPendings(nc);
            long pr = tf.lookupProcessed(nc);

            System.out.println("### Procesados: " + pr);
            System.out.println("### Pendientes: " + pe);

            if (pr == 0) {
                long seq = tf.lookupSequence();
                cp.setSequence(seq);
                System.out.println("### Sequiencia: " + cp.getSequence());

                Date startDate = new Date();
                String fileHour = fileDateFormat.format(startDate);
                String finalFileName = cp.getFileName() + fileHour + ".csv";

                cp.setFinalFileName(finalFileName);
                System.out.println("### Nombre de archivo: " + cp.getFinalFileName());

                cp.setResult("PROC");
                cp.setObservations("Interfaz en Proceso...");

                tf.insertStartControl(nc, cp);

                if (pe == 0) {
                    List<List<String>> llt = tf.processInterface(nc, cp);
                    //List<List<String>> llt = new ArrayList<>();
                    nc.setRows(Integer.toString(llt.size()));

                    onWriteFile(llt, cp);
                    
                    cp.setResultFile("Inicia envío de archivo");
                    tf.insertStartFile(cp);

                    //SFTPSenderTest sftp = new SFTPSenderTest(cp);
                    SFTPSender sftp = new SFTPSender(cp);
                    sftp.onUploadFile();
                    
                    cp.setResultFile("Finaliza envío de archivo");
                    tf.insertEndFile(cp);

                    Date endDate = new Date();
                    cp.setResult("OK");
                    cp.setObservations("Ok Finaliza " + dateFormat.format(endDate) + " con " + nc.getRows() + " Lecturas");
                } else {
                    cp.setResult("ERR");
                    cp.setObservations("Error ! Ciclo " + nc.getSubsidiary() + "-" + nc.getCycles() + " Proceso: " + nc.getProcessDate()
                            + " No genera Interface, Hay " + pe + " Lecturas Pendientes..");
                }
                tf.insertEndControl(nc, cp);
            }
            System.out.println("##########################################################################");
        } catch (Exception ex) {
            System.out.println("### HA OCURRIDO UN ERROR: " + ex.getMessage());
        }
    }

    public void onWriteFile(List<List<String>> llt, ControlParameters cp) {
        try {

            System.out.println("### Escribir csv...");

            String filePath = cp.getLocalFilePath() + cp.getFinalFileName();

            try (FileWriter csv = new FileWriter(filePath)) {
                csv.append("TASK_ID" + cp.getSeparator());
                csv.append("TASK_TYPE" + cp.getSeparator());
                csv.append("LATITUDE" + cp.getSeparator());
                csv.append("LONGITUDE" + cp.getSeparator());
                csv.append("COUNTRY_CODE" + cp.getSeparator());
                csv.append("SUPPLY_ID" + cp.getSeparator());
                csv.append("SUPPLY_TYPE" + cp.getSeparator());
                csv.append("TASK_EXECUTION_DATE_FROM" + cp.getSeparator());

                csv.append("TASK_EXECUTION_DATE_TO" + cp.getSeparator());
                csv.append("POM_ID" + cp.getSeparator());
                csv.append("POM_TYPE" + cp.getSeparator());
                csv.append("METER_BRAND" + cp.getSeparator());
                csv.append("METER_MODEL" + cp.getSeparator());
                csv.append("METER_TYPE" + cp.getSeparator());
                csv.append("METER_SERIAL_NUMBER" + cp.getSeparator());
                csv.append("METER_BARCODE" + cp.getSeparator());
                csv.append("SYSTEM_NOTES" + cp.getSeparator());
                csv.append("READINGS_INFO" + cp.getSeparator());

                csv.append("\n");

                for (List<String> lst : llt) {
                    for (String st : lst) {

                        if (st == null) {
                            st = "";
                        }
                        if (st.equals("null")) {
                            st = "";
                        }
                        st = st.replaceAll("\\n", "");
                        csv.append(st + cp.getSeparator());

                    }
                    csv.append("\n");
                }

                csv.flush();
                csv.close();
            }

            System.out.println("### Finaliza escritura csv satisfactoriamente...");

        } catch (IOException ex) {
            System.out.println("### HA OCURRIDO UN ERROR: " + ex.getMessage());
        }
    }

    public void sendFileTest() {
        try {
            DateFormat dateFormatFile = new SimpleDateFormat("yyyyMMddHHmmss");
            Date fileDate = new Date();
            String date = dateFormatFile.format(fileDate);

            ControlParameters cp = new ControlParameters();
            cp.setFinalFileName("FILE_" + date + ".txt");

            String localFilePath = cp.getLocalFilePath();
            String fileName = cp.getFinalFileName();

            String str = fileDate.toString() + ".... hello world !!";
            BufferedWriter writer = new BufferedWriter(new FileWriter(localFilePath + fileName));

            writer.write(str);
            writer.close();

            SFTPSender sftp = new SFTPSender(cp);
            sftp.onUploadFile();

        } catch (IOException ex) {
            System.out.println("### HA OCURRIDO UN ERROR: " + ex.getMessage());
        }
    }


}
