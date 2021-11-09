package com.csc301group36.Covid19API.Entities;

public enum TimeSeriesRequestType {
    death("time_series_death"),
    confirmed("time_series_confirm"),
    recovered("time_series_recovered"),
    active(null);

    public String fileName;
    TimeSeriesRequestType(String fn){
        fileName = fn;
    }
}
