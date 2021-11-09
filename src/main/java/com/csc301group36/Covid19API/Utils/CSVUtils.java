package com.csc301group36.Covid19API.Utils;

import com.csc301group36.Covid19API.Entities.Conditions;
import com.csc301group36.Covid19API.Entities.DBType;
import com.csc301group36.Covid19API.Exceptions.InvalidDataTypeError;
import com.csc301group36.Covid19API.Exceptions.RecordGetError;
import com.csc301group36.Covid19API.Exceptions.InternalError;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CSVUtils {
    @Autowired
    private DBManager dbManager;
    @Autowired
    private DateUtils dateUtils;

    public CSVUtils(){
    }

    public List<CSVRecord> query(Conditions conditions) throws InternalError {
        if(conditions.getType() == DBType.TimeSeries){
            return getTimeSeriesQueryResult(conditions);
        }
        else{
            return getDailyReportQueryResult(conditions);
        }
    }

    private List<CSVRecord> getTimeSeriesQueryResult(Conditions conditions)
            throws InternalError {
        File f = dbManager.getTimeSeriesFile(conditions.getTimeSeriesRequestType());
        Iterable<CSVRecord> records = getRecords(f);
        List<CSVRecord> qualified = new ArrayList<>();
        for(CSVRecord record : records){
            if(conditions.areSatisfied(record)) qualified.add(record);
        }

        return qualified;
    }

    private List<CSVRecord> getDailyReportQueryResult(Conditions conditions) throws InternalError {
        List<File> fs = getFilesByRange(conditions);
        List<CSVRecord> result = new ArrayList<>();
        for(File f : fs){
            Iterable<CSVRecord> records = getRecords(f);
            for(CSVRecord record : records){
                if(conditions.areSatisfied(record)) result.add(record);
            }
        }
        return result;
    }

    private List<File> getFilesByRange(Conditions conditions) throws InternalError {
        Date startD = dateUtils.stringToDate(conditions.getStartDate(), conditions.getType());
        Date endD = dateUtils.stringToDate(conditions.getEndDate(), conditions.getType());
        List<File> result = new ArrayList<>();
        for(String date : dbManager.getDailyFileDates()){
            Date dateD = dateUtils.stringToDate(date, conditions.getType());
            if(dateUtils.isInBetween(startD, endD, dateD)) result.add(dbManager.getDailyFile(date));
        }
        return result;
    }

    private Iterable<CSVRecord> getRecords(File f) throws InternalError {
        try{
            Reader r = new FileReader(f);
            return CSVFormat.DEFAULT.parse(r);
        }catch (IOException e){
            throw new InternalError("Server Internal Error: parse failed in getRecords() in CSVUtils class.");
        }
    }

}
