package com.csc301group36.Covid19API.Utils;

import com.csc301group36.Covid19API.Entities.DBType;
import com.csc301group36.Covid19API.Exceptions.InternalError;
import lombok.NoArgsConstructor;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
@NoArgsConstructor
public class CSVFormatChecker {
    @Autowired
    private CSVManager csvManager;
    @Autowired
    private DateUtils dateUtils;

    private final List<String> timeSeriesOverrideHeaders = Arrays.asList("Province/State", "Country/Region", "Lat", "Long");
    private final List<String> dailyReportsHeaders = Arrays.asList(
            "FIPS", "Admin2", "Province_State", "Country_Region", "Last_Update", "Lat", "Long_",
            "Confirmed", "Deaths", "Recovered", "Active", "Combined_Key", "Incidence_Rate", "Case-Fatality_Ratio");

    public boolean isValidTimeSeriesOverride(List<CSVRecord> records) throws InternalError{
        return isValidTimeSeriesOverrideHelper(records);
    }

    public boolean isValidTimeSeriesUpdate(List<CSVRecord> records, Collection<String> headerSet) throws InternalError{
        return isValidTimeSeriesUpdateHelper(records, headerSet);
    }

    public boolean isValidDailyReports(List<CSVRecord> records) throws InternalError{
        return isValidDailyReportsHelper(records);
    }

    // Helpers
    private boolean isValidDailyReportsHelper(List<CSVRecord> records) throws InternalError{
        return formatCheckHelper(records, dailyReportsHeaders);
    }

    private boolean isValidTimeSeriesUpdateHelper(List<CSVRecord> records, Collection<String> headerSet) throws InternalError{
        return formatCheckHelper(records, headerSet) && hasValidDateFields(records);
    }

    private boolean isValidTimeSeriesOverrideHelper(List<CSVRecord> records) throws InternalError{
        Collection<String> newHeaders = csvManager.getHeaders(records);
        for(String header : timeSeriesOverrideHeaders){
            if(!newHeaders.contains(header)) throw new InternalError("Invalid CSV format. Fields must contain at least: "
                    + timeSeriesOverrideHeaders.toString());
        }
        return hasValidDateFields(records);
    }

    private boolean formatCheckHelper(List<CSVRecord> records, Collection<String> headers) throws InternalError{
        if(!records.isEmpty()){
            CSVRecord record0 = records.get(0);
            Collection<String> newHeaders = csvManager.getHeaders(records);
            if(newHeaders.size() != headers.size()) throw new InternalError("Invalid CSV format. Fields must be: "
                    + headers.toString());
            for(String h : newHeaders) if(!headers.contains(h)) throw new InternalError("Invalid CSV format. Fields must be: "
                    + headers.toString());
            try{
                for(CSVRecord record : records){
                    for(String header : headers){
                        record.get(header);
                    }
                }
            }catch (Exception e) {throw new InternalError("Records are not consistent with fields, please check your csv format.");}
        }
        return true;
    }

    private boolean hasValidDateFields(List<CSVRecord> records) throws InternalError{
        Collection<String> headers = csvManager.getHeaders(records);
        for(String header : headers){
            if(timeSeriesOverrideHeaders.contains(header)) continue;
            try{
                if(!dateUtils.isValidDate(header, DBType.TimeSeries)) return false;
            }catch (Exception e){
                throw new InternalError("Operation denied. We found some field(s) with wrong date format.");
            }
        }
        return true;
    }
}
