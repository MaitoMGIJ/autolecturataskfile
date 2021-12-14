    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ayesa.autolecturataskfile.dao;


import com.ayesa.autolecturataskfile.ControlParameters;
import com.ayesa.autolecturataskfile.NewCycles;
import com.ayesa.autolecturataskfile.dto.AIReadingInfoDTO;
import com.google.gson.Gson;
import java.sql.CallableStatement;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import oracle.jdbc.OracleTypes; 


/**
 *
 * @author felipe
 */
public class TaskFileDaoImpl implements TaskFileDao {
    
    
    private final Connection conn;

    public TaskFileDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public ControlParameters getControlParameters() throws Exception {
        ControlParameters cp = new ControlParameters();
        
        String procedure = "BEGIN AULEC_FLOW1_CONTROL(?); END;";
        
        try (CallableStatement callableStatement = conn.prepareCall(procedure)) {
            callableStatement.registerOutParameter(1, OracleTypes.CURSOR);
            callableStatement.execute();
            
            try (ResultSet rs = (ResultSet)callableStatement.getObject(1)) {
                while (rs.next()){
                    switch (rs.getString(1)) {
                        case "001":
                            cp.setSeparator(rs.getString(2));
                            break;
                        case "002":
                            cp.setFileName(rs.getString(2));
                            break;
                        case "003":
                            cp.setSFTPServer(rs.getString(2));
                            break;
                        case "004":
                            cp.setSFTPUser(rs.getString(2));
                            break;
                        case "005":
                            cp.setSFTPKeyFilePath(rs.getString(2));
                            break;
                        case "006":
                            cp.setSFTPFilePath(rs.getString(2));
                            break;
                        default:
                            break;
                    }
                }
                rs.close();
            }
        }
        return cp;
    }

    @Override
    public List<NewCycles> lookupNewCycles() throws Exception {
        List<NewCycles> lnc = new ArrayList<>();
        
        String procedure = "BEGIN AULEC_FLOW1_CICLOS(?); END;";
        
        try (CallableStatement callableStatement = conn.prepareCall(procedure)) {
            callableStatement.registerOutParameter(1, OracleTypes.CURSOR);
            callableStatement.execute();
            
            try (ResultSet rs = (ResultSet)callableStatement.getObject(1)) {
                while (rs.next()){
                    NewCycles nc = new NewCycles();
                    nc.setProcessDate(rs.getString(1));
                    nc.setFileDate(rs.getString(2));
                    nc.setSubsidiary(rs.getString(3));
                    nc.setCycles(rs.getString(4));
                    nc.setRows(rs.getString(5));
                    lnc.add(nc);
                }
                rs.close();
            }
        }
        return lnc;
    }

    @Override
    public long lookupPendings(NewCycles nc) throws Exception {
        int p = 0;
        
        String procedure = "BEGIN AULEC_FLOW1_PENDIENTES(?,?,?,?); END;";
        
        try (CallableStatement callableStatement = conn.prepareCall(procedure)) {
            callableStatement.registerOutParameter(1, OracleTypes.INTEGER);
            callableStatement.setString(2, nc.getProcessDate());
            callableStatement.setString(3, nc.getSubsidiary());
            callableStatement.setString(4, nc.getCycles());
            callableStatement.execute();
            
            p = (Integer)callableStatement.getObject(1);
        }
        return p;
    }

    @Override
    public long lookupProcessed(NewCycles nc) throws Exception {
        int p = 0;
        
        String procedure = "BEGIN AULEC_FLOW1_PROCESADOS(?,?,?,?); END;";
        
        try (CallableStatement callableStatement = conn.prepareCall(procedure)) {
            callableStatement.registerOutParameter(1, OracleTypes.INTEGER);
            callableStatement.setString(2, nc.getProcessDate());
            callableStatement.setString(3, nc.getSubsidiary());
            callableStatement.setString(4, nc.getCycles());
            callableStatement.execute();
            
            p = (Integer)callableStatement.getObject(1);
        }
        return p;
    }

    @Override
    public long lookupSequence() throws Exception {
        long s = 0;
        
        String query = "SELECT AOF.SEQ_LECSSB0151.nextval FROM dual ";
        
        try (PreparedStatement ps = conn.prepareStatement(query)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    s = rs.getLong(1);
                }
            }
        }
        return s;
    }

    @Override
    public void insertStartControl(NewCycles nc, ControlParameters cp) throws Exception {

        String query = "INSERT INTO AOF.LECSSB0151 (ID_INTERFACE,TIPO_INTERFACE, "
                + "FECHA_PROCESO,SUCURSAL,CICLO,INI_PROCESO,NOMBRE_ARCHIVO, "
                + "CANTIDAD_REGISTROS,RESULTADO,OBSERVACIONES) "
                + " VALUES (?,?,to_date(?,'dd/mm/yyyy'),?,?,sysdate,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, Long.toString(cp.getSequence()));
            ps.setString(2, cp.getMode());
            ps.setString(3, nc.getProcessDate());
            ps.setString(4, nc.getSubsidiary());
            ps.setString(5, nc.getCycles());
            ps.setString(6, cp.getFinalFileName());
            ps.setString(7, nc.getRows());
            ps.setString(8, cp.getResult());
            ps.setString(9, cp.getObservations());

            ps.executeQuery();
        }
    }

    @Override
    public void insertEndControl(NewCycles nc, ControlParameters cp) throws Exception {
        String query = "UPDATE AOF.LECSSB0151 SET FIN_PROCESO = sysdate, RESULTADO = ?, " +
                        "OBSERVACIONES = ?, CANTIDAD_REGISTROS = ? WHERE ID_INTERFACE = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, cp.getResult());
            ps.setString(2, cp.getObservations());
            ps.setString(3, nc.getRows());
            ps.setString(4, Long.toString(cp.getSequence()));            

            ps.executeQuery();
        }
    }
    
    @Override
    public void insertStartFile(ControlParameters cp) throws Exception {

        String query = "UPDATE LECSSB0151 SET INI_ENVIO_ARCHIVO = sysdate,  " +
                        " ARCHIVO_ENVIADO = ? WHERE ID_INTERFACE = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, cp.getResultFile());
            ps.setString(2, Long.toString(cp.getSequence()));            

            ps.executeQuery();
        }
    }
    
    
    @Override
    public void insertEndFile(ControlParameters cp) throws Exception {

        String query = "UPDATE LECSSB0151 SET FIN_ENVIO_ARCHIVO = sysdate,  " +
                        " ARCHIVO_ENVIADO = ? WHERE ID_INTERFACE = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, cp.getResultFile());
            ps.setString(2, Long.toString(cp.getSequence()));
            ps.executeQuery();
        }
    }
       
    @Override
    public List<List<String>> processInterface(NewCycles nc, ControlParameters cp) throws Exception {
        List<List<String>> llt = new ArrayList<>();
        
        String procedure = "BEGIN AULEC_FLOW1_V2(?,?,?,?); END;";
        
        try (CallableStatement callableStatement = conn.prepareCall(procedure)) {
            callableStatement.registerOutParameter(1, OracleTypes.CURSOR);
            callableStatement.setString(2, nc.getSubsidiary());
            callableStatement.setString(3, nc.getCycles());
            callableStatement.setString(4, nc.getProcessDate());
            callableStatement.execute();
            
            int i = 0;
            try (ResultSet rs = (ResultSet)callableStatement.getObject(1)) {
                while (rs.next()){
                    List<String> lst = new ArrayList<>();
                    lst.add(UUID.randomUUID().toString());
                    //lst.add(rs.getString(1));
                    lst.add(rs.getString(2));
                    lst.add(rs.getString(3));
                    lst.add(rs.getString(4));
                    lst.add(rs.getString(5));
                    lst.add(rs.getString(6));
                    lst.add(rs.getString(7));
                    lst.add(rs.getString(8));
                    lst.add(rs.getString(9));
                    lst.add(rs.getString(10));
                    lst.add(rs.getString(11));
                    lst.add(rs.getString(12));
                    lst.add(rs.getString(13));
                    lst.add(rs.getString(14));
                    lst.add(rs.getString(15));
                    lst.add(rs.getString(16));
                    lst.add(rs.getString(17));
                    
                    String supplayId = rs.getString(6);
                    List<AIReadingInfoDTO> lrjson = lookupReadings(nc, supplayId);

                    Gson gson = new Gson();
                    String readingsJson = gson.toJson(lrjson);
                    lst.add(readingsJson);
                    
                    lst.add(rs.getString(18));

                    llt.add(lst);
                    
                    i++;
                    if ( i == 10000 ) {
                        i = 0;
                        System.out.println("### Registros procesados : " + llt.size());
                    }
                }
            }   
        }
        System.out.println("### Total de registros : " + llt.size());
        return llt;
    }

    @Override
    public List<AIReadingInfoDTO> lookupReadings(NewCycles nc, String supplayId) throws Exception {
        List<AIReadingInfoDTO> lairi = new ArrayList<>();
        
        String procedure = "BEGIN AULEC_FLOW1_LECTURAS(?,?,?); END;";
        
        try (CallableStatement callableStatement = conn.prepareCall(procedure)) {
            callableStatement.registerOutParameter(1, OracleTypes.CURSOR);
            callableStatement.setString(2, supplayId);
            callableStatement.setString(3, nc.getProcessDate());
            callableStatement.execute();
            
            try (ResultSet rs = (ResultSet)callableStatement.getObject(1)) {
                while (rs.next()){
                    AIReadingInfoDTO airi = new AIReadingInfoDTO();

                    airi.setReadingType(rs.getString(1));
                    airi.setReadingDes(rs.getString(2));
                    airi.setMinValue(rs.getLong(3));
                    airi.setMaxValue(rs.getLong(4));
                    airi.setLastValue(rs.getLong(5));
                    airi.setLastDate(rs.getString(6));
                    airi.setConsAvg(rs.getLong(7));

                    lairi.add(airi);
                }
            }
        }
        return lairi;
    }
    
}
