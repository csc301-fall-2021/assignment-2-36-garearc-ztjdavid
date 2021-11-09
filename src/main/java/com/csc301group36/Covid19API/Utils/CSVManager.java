package com.csc301group36.Covid19API.Utils;

import com.csc301group36.Covid19API.Entities.Conditions;
import com.csc301group36.Covid19API.Entities.DBType;
import com.csc301group36.Covid19API.Entities.TimeSeriesRequestType;
import com.csc301group36.Covid19API.Exceptions.InternalError;
import com.csc301group36.Covid19API.Exceptions.InvalidCSVFormatError;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

@Component
public class CSVManager {
    @Autowired
    private DBManager dbManager;
    @Autowired
    private DateUtils dateUtils;
    @Autowired
    private CSVFormatChecker csvFormatChecker;

    private final CSVFormat formatter = CSVFormat.Builder.create(CSVFormat.DEFAULT)
            .setHeader()
            .setDelimiter(',')
            .build();


    public CSVManager(){
    }

    public CSVFormat getFormatter() {
        return formatter;
    }

    public CSVPrinter getPrinter(Collection<String> headers, Writer writer) throws InternalError{
        try{
            CSVFormat formatter = CSVFormat.Builder.create(CSVFormat.DEFAULT)
                    .setHeader(headers.toArray(new String[0]))
                    .setDelimiter(',')
                    .build();
            return new CSVPrinter(writer, formatter);
        }catch (Exception e){throw new InternalError("Failed to create CSVPrinter.");}
    }

    public void overrideTimeSeriesFile(String csvString, TimeSeriesRequestType type) throws InternalError{
        List<CSVRecord> records = getRecords(csvString);
        if(csvFormatChecker.isValidTimeSeriesOverride(records)){
            dbManager.writeToTimeSeriesFile(csvString, type);
        }
    }
    public void updateTimeSeriesFile(String csvString, TimeSeriesRequestType type) throws InternalError{
        List<CSVRecord> newRecords = getRecords(csvString);
        Collection<String> headers = getHeaders(newRecords);
        if(csvFormatChecker.isValidTimeSeriesUpdate(newRecords, headers)){
            List<CSVRecord> oldRecords = getRecords(dbManager.getTimeSeriesFile(type));
            // Insert all oldRecords first
            List<CSVRecord> mergedRecords = new ArrayList<>(oldRecords);
            try {
                // merge records by replacing oldRecords
                for(CSVRecord newRecord : newRecords){
                    for(CSVRecord oldRecord : oldRecords){
                        if(newRecord.get("Province/State").equals(oldRecord.get("Province/State")) &&
                                newRecord.get("Country/Region").equals(oldRecord.get("Country/Region")) &&
                                newRecord.get("Lat").equals(oldRecord.get("Lat")) &&
                                newRecord.get("Long").equals(oldRecord.get("Long"))
                        ) mergedRecords.set(mergedRecords.indexOf(oldRecord), newRecord);
                        else mergedRecords.add(newRecord);
                    }
                }
                // Write merged records into StringWriter
                StringWriter writer = new StringWriter();
                CSVPrinter printer = getPrinter(headers, writer);
                printer.printRecords(mergedRecords);
                dbManager.writeToTimeSeriesFile(writer.toString(), type);
            }catch (Exception e) {throw new InternalError("Database error(Failed to write to file.)");}
        }
    }

    public List<CSVRecord> query(Conditions conditions) throws InternalError {
        if(conditions.getType() == DBType.TimeSeries){
            return getTimeSeriesQueryResult(conditions);
        }
        else{
            return getDailyReportQueryResult(conditions);
        }
    }

    public List<CSVRecord> getRecords(File f) throws InternalError {
        try{
            Reader r = new FileReader(f);
            List<CSVRecord> result = new ArrayList<>();
            formatter.parse(r).iterator().forEachRemaining(result::add);
            return result;
        }catch (IOException e){
            throw new InvalidCSVFormatError(f.getName());
        }
    }

    public List<CSVRecord> getRecords(String csvString) throws InternalError {
        try{
            Reader r = new StringReader(csvString);
            List<CSVRecord> result = new ArrayList<>();
            formatter.parse(r).iterator().forEachRemaining(result::add);
            return result;
        }catch (IOException e){
            throw new InternalError("Invalid CSV format, please check and update a correct one.");
        }
    }

    private Collection<String> getHeaders(List<CSVRecord> records) throws InternalError{
        if(!records.isEmpty()){
            CSVRecord record = records.get(0);
            return record.toMap().keySet();
        }
        return Collections.emptyList();
    }


    // Helpers
    private List<CSVRecord> getTimeSeriesQueryResult(Conditions conditions)
            throws InternalError {
        File f = dbManager.getTimeSeriesFile(conditions.getTimeSeriesRequestType());
        List<CSVRecord> records = getRecords(f);
        List<CSVRecord> qualified = new ArrayList<>();
        for(CSVRecord record : records){
            if(conditions.areSatisfied(record, f)) qualified.add(record);
        }

        return qualified;
    }

    private List<CSVRecord> getDailyReportQueryResult(Conditions conditions) throws InternalError {
        List<File> fs = getFilesByRange(conditions);
        List<CSVRecord> result = new ArrayList<>();
        for(File f : fs){
            List<CSVRecord> records = getRecords(f);
            for(CSVRecord record : records){
                if(conditions.areSatisfied(record, f)) result.add(record);
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

}
