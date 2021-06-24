/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ayesa.autolecturataskfile;

/**
 *
 * @author felipe
 */
public class ControlParameters {
    
    private String localFilePath = "resources/";
    private String finalFileName;
    
    private String separator;
    private String fileName;
    private String SFTPServer;
    private String SFTPUser;
    private String SFTPKeyFilePath;
    private String SFTPFilePath;
    
    private String mode;
    private long sequence;
    private String result;
    private String observations;
    private String resultFile;
    
    
    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getFinalFileName() {
        return finalFileName;
    }

    public void setFinalFileName(String finalFileName) {
        this.finalFileName = finalFileName;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSFTPServer() {
        return SFTPServer;
    }

    public void setSFTPServer(String SFTPServer) {
        this.SFTPServer = SFTPServer;
    }

    public String getSFTPUser() {
        return SFTPUser;
    }

    public void setSFTPUser(String SFTPUser) {
        this.SFTPUser = SFTPUser;
    }

    public String getSFTPKeyFilePath() {
        return SFTPKeyFilePath;
    }

    public void setSFTPKeyFilePath(String SFTPKeyFilePath) {
        this.SFTPKeyFilePath = SFTPKeyFilePath;
    }

    public String getSFTPFilePath() {
        return SFTPFilePath;
    }

    public void setSFTPFilePath(String SFTPFilePath) {
        this.SFTPFilePath = SFTPFilePath;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
    
    public String getResultFile() {
        return resultFile;
    }

    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
    }
    
}
