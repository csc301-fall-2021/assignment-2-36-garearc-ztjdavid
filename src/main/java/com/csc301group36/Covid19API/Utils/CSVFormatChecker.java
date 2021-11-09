package com.csc301group36.Covid19API.Utils;

import com.csc301group36.Covid19API.Exceptions.InternalError;
import lombok.NoArgsConstructor;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
@NoArgsConstructor
public class CSVFormatChecker {
    @Autowired
    private CSVManager csvManager;

    private final List<String> timeSeriesOverrideHeaders = Arrays.asList("Province/State", "Country/Region", "Lat", "Long");
    private final List<String> dailyReportsHeaders = Arrays.asList(
            "FIPS", "Admin2", "Province_State", "Country_Region", "Last_Update", "Lat", "Long_",
            "Confirmed", "Deaths", "Recovered", "Active", "Combined_Key", "Incident_Rate", "Case_Fatality_Ratio");

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
        return formatCheckHelper(records, headerSet);
    }

    private boolean isValidTimeSeriesOverrideHelper(List<CSVRecord> records) throws InternalError{
        return formatCheckHelper(records, timeSeriesOverrideHeaders);
    }

    private boolean formatCheckHelper(List<CSVRecord> records, Collection<String> headers) throws InternalError{
        if(!records.isEmpty()){
            CSVRecord record = records.get(0);
            try{
                for(String header : headers){
                    record.get(header);
                }
            }catch (Exception e) {throw new InternalError("Invalid CSV format. Fields must be: "
                    + headers.toString());}
        }
        return true;
    }
}
