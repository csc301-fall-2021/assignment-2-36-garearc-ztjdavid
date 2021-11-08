package com.csc301group36.Covid19API.Entities;

import com.csc301group36.Covid19API.Exceptions.RequestError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.CSVRecord;

@AllArgsConstructor
@Getter
@Setter
public class Conditions {
    private DBType type;
    private String country;
    private String state;
    private String combinedKeys;
    private String startDate;
    private String endDate;
    private TimeSeriesRequestType timeSeriesRequestType;
    private DailyReportRequestType dailyReportRequestType;

    public boolean isSatisfied(CSVRecord r) throws RequestError {
        try{
            if(type == DBType.TimeSeries){
                if(!r.get("Country/Region").equals(country)) return false;
                if(!r.get("Province/State").equals(state)) return false;
                return true;
            }
            if(type == DBType.DailyReports){
                if(!r.get("Country_Region").equals(country)) return false;
                if(!r.get("Province_State").equals(state)) return false;
                if(!r.get("Combined_Key").equals(combinedKeys)) return false;
                return true;
            }
        }catch (Exception e) {throw new RequestError("Server Internal Error: method isSatisfied() cannot get value.");}
        return false;
    }
}
