package com.csc301group36.Covid19API.Entities;

import com.csc301group36.Covid19API.Exceptions.InternalError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.csv.CSVRecord;

@AllArgsConstructor
@NoArgsConstructor
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

    public boolean areSatisfied(CSVRecord r) throws InternalError {
        try{
            if(type == DBType.TimeSeries){
                if(country != null && !r.get("Country/Region").equalsIgnoreCase(country)) return false;
                if(state != null && !r.get("Province/State").equalsIgnoreCase(state)) return false;
                return true;
            }
            if(type == DBType.DailyReports){
                if(country != null && !r.get("Country_Region").equalsIgnoreCase(country)) return false;
                if(state != null && !r.get("Province_State").equalsIgnoreCase(state)) return false;
                if(combinedKeys != null && !r.get("Combined_Key").equalsIgnoreCase(combinedKeys)) return false;
                return true;
            }
        }catch (Exception e) {throw new InternalError("Server Internal Error: method isSatisfied() cannot get value.");}
        return false;
    }

}
