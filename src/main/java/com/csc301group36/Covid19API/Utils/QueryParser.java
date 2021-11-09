package com.csc301group36.Covid19API.Utils;

import com.csc301group36.Covid19API.Entities.QueryResponse;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QueryParser {
    public String parseCSV(List<CSVRecord> records){
        return "";
    }

    public QueryResponse parseJSON(List<CSVRecord> records){
        return new QueryResponse(null);
    }
}
