/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ayesa.autolecturataskfile.dto;

/**
 *
 * @author felipe
 */
public class AIReadingInfoDTO {
    
    private String readingType;
    private String readingDes;
    private Long minValue;
    private Long maxValue;
    private Long lastValue;
    private String lastDate;
    private Long consAvg;
    

    public String getReadingType() {
        return readingType;
    }

    public void setReadingType(String readingType) {
        this.readingType = readingType;
    }

    public String getReadingDes() {
        return readingDes;
    }

    public void setReadingDes(String readingDes) {
        this.readingDes = readingDes;
    }

    public Long getMinValue() {
        return minValue;
    }

    public void setMinValue(Long minValue) {
        this.minValue = minValue;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Long maxValue) {
        this.maxValue = maxValue;
    }

    public Long getLastValue() {
        return lastValue;
    }

    public void setLastValue(Long lastValue) {
        this.lastValue = lastValue;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public Long getConsAvg() {
        return consAvg;
    }

    public void setConsAvg(Long consAvg) {
        this.consAvg = consAvg;
    }
    
}
