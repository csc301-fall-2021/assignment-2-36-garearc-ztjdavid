package com.csc301group36.Covid19API.Utils;

import lombok.NoArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

@Component
@NoArgsConstructor
public class CSVFormatChecker {
    public boolean isValidTimeSeries(File f){
        if(!f.exists()) return false;
        Iterable<CSVRecord> records = null;
        try{
            Reader r = new FileReader(f);
            records = CSVFormat.DEFAULT.parse(r);
            CSVRecord record = records.iterator().next();
            record.get("Province/State");
            record.get("Country/Region");
            record.get("Lat");
            record.get("Long");
        }catch (Exception e) {return false;}
        return true;
    }

    public boolean isValidDailyReports(File f){
        if(!f.exists()) return false;
        Iterable<CSVRecord> records = null;
        try{
            Reader r = new FileReader(f);
            records = CSVFormat.DEFAULT.parse(r);
            CSVRecord record = records.iterator().next();
            record.get("FIPS");
            record.get("Admin2");
            record.get("Province_State");
            record.get("Country_Region");
            record.get("Last_Update");
            record.get("Lat");
            record.get("Long_");
            record.get("Confirmed");
            record.get("Deaths");
            record.get("Recovered");
            record.get("Active");
            record.get("Combined_Key");
            record.get("Incident_Rate");
            record.get("Case_Fatality_Ratio");
        }catch (Exception e) {return false;}
        return true;
    }
}
