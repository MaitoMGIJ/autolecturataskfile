/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ayesa.autolecturataskfile.dao;

import com.ayesa.autolecturataskfile.ControlParameters;
import com.ayesa.autolecturataskfile.NewCycles;
import com.ayesa.autolecturataskfile.dto.AIReadingInfoDTO;
import java.util.List;

/**
 *
 * @author felipe
 */
public interface TaskFileDao {
    
    public ControlParameters getControlParameters() throws Exception;
    
    public List<NewCycles> lookupNewCycles() throws Exception;
    
    public long lookupPendings(NewCycles nc) throws Exception;
    
    public long lookupProcessed(NewCycles nc) throws Exception;    
    
    public long lookupSequence() throws Exception;
    
    public void insertStartControl(NewCycles nc, ControlParameters cp) throws Exception;
    
    public void insertEndControl(NewCycles nc, ControlParameters cp) throws Exception;
    
    public void insertStartFile(ControlParameters cp) throws Exception;
    
    public void insertEndFile(ControlParameters cp) throws Exception;
            
    public List<List<String>> processInterface(NewCycles nc, ControlParameters cp) throws Exception;
    
    public List<AIReadingInfoDTO> lookupReadings(NewCycles nc, String supplayId) throws Exception;
    
}
