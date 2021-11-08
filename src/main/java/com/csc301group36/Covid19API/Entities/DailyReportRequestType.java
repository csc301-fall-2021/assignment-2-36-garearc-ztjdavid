package com.csc301group36.Covid19API.Entities;

public enum DailyReportRequestType {
    death("Deaths"),
    confirmed("Confirmed"),
    recovered("Recovered"),
    active("Active");

    public String colName;
    DailyReportRequestType(String col){
        colName = col;
    }
}
