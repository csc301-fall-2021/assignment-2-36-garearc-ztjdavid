package com.csc301group36.Covid19API.Utils;

import com.csc301group36.Covid19API.Entities.Conditions;
import com.csc301group36.Covid19API.Entities.DBType;
import com.csc301group36.Covid19API.Exceptions.InvalidDataTypeError;
import com.csc301group36.Covid19API.Exceptions.RecordGetError;
import com.csc301group36.Covid19API.Exceptions.RequestError;
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

    public Integer query(Conditions conditions) throws RequestError {
        if(conditions.getType() == DBType.TimeSeries){
            return getTimeSeriesQueryResult(conditions);
        }
        else{
            return getDailyReportQueryResult(conditions);
        }
    }

    private Integer getTimeSeriesQueryResult(Conditions conditions)
            throws RequestError{
        File f = dbManager.getTimeSeriesFile(conditions.getTimeSeriesRequestType());
        Iterable<CSVRecord> records = getRecords(f);
        List<CSVRecord> qualified = new ArrayList<>();
        for(CSVRecord record : records){
            if(conditions.isSatisfied(record)) qualified.add(record);
        }
        return getTimeSeriesQueryResultHelper(qualified, conditions, f);
    }

    private Integer getDailyReportQueryResult(Conditions conditions) throws RequestError{
        int sum = 0;
        List<File> fs = getFilesByRange(conditions);
        for(File f : fs){
            Iterable<CSVRecord> records = getRecords(f);
            for(CSVRecord record : records){
                if(!conditions.isSatisfied(record)) continue;
                try{
                    sum += Integer.parseInt(record.get(conditions.getDailyReportRequestType().colName));
                }catch (NumberFormatException ne){
                    throw new InvalidDataTypeError(record.toString(), f.getName());
                }catch (Exception e){
                    throw new RecordGetError(conditions.getDailyReportRequestType().colName);
                }
            }
        }
        return sum;
    }

    private int getTimeSeriesQueryResultHelper(List<CSVRecord> qualified, Conditions conditions, File f)
            throws RequestError{
        if(qualified.size() == 0) return 0;
        int sum = 0;
        List<String> keys = new ArrayList<>(qualified.get(0).toMap().keySet());
        List<String> cols = new ArrayList<>();
        Date startD = dateUtils.stringToDate(conditions.getStartDate(), conditions.getType());
        Date endD = dateUtils.stringToDate(conditions.getEndDate(), conditions.getType());
        // Find all date columns in between.
        for(String date : keys){
            if(dateUtils.isValidDate(date, conditions.getType())){
                Date dateD = dateUtils.stringToDate(date, conditions.getType());
                if(dateUtils.isInBetween(startD, endD, dateD)) cols.add(date);
            }
        }
        // Sum up
        for(CSVRecord record : qualified){
            for (String col : cols){
                try{
                    sum += Integer.parseInt(record.get(col));
                }catch (NumberFormatException e){
                    throw new InvalidDataTypeError(record.toString(), f.getName());
                }catch (Exception e){
                    throw new RecordGetError(col);
                }
            }
        }
        return sum;
    }

    private List<File> getFilesByRange(Conditions conditions) throws RequestError{
        Date startD = dateUtils.stringToDate(conditions.getStartDate(), conditions.getType());
        Date endD = dateUtils.stringToDate(conditions.getEndDate(), conditions.getType());
        List<File> result = new ArrayList<>();
        for(String date : dbManager.getDailyFileDates()){
            Date dateD = dateUtils.stringToDate(date, conditions.getType());
            if(dateUtils.isInBetween(startD, endD, dateD)) result.add(dbManager.getDailyFile(date));
        }
        return result;
    }

    private Iterable<CSVRecord> getRecords(File f) throws RequestError{
        try{
            Reader r = new FileReader(f);
            return CSVFormat.DEFAULT.parse(r);
        }catch (IOException e){
            throw new RequestError("Server Internal Error: parse failed in getRecords() in CSVUtils class.");
        }
    }

}
