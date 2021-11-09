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

    /**
     * Get a CSVPrinter with DEFAULT CSV format and ',' as delimiter.
     * @param headers Headers(Fields) to print when use this printer to print CSV files. Headers must be consistent
     * with the records when printing.
     * @param writer A JAVA Writer that defines where the output should go.
     * */
    public CSVPrinter getPrinter(Collection<String> headers, Writer writer) throws InternalError{
        try{
            CSVFormat formatter = CSVFormat.Builder.create(CSVFormat.DEFAULT)
                    .setHeader(headers.toArray(new String[0]))
                    .setDelimiter(',')
                    .build();
            return new CSVPrinter(writer, formatter);
        }catch (Exception e){throw new InternalError("Failed to create CSVPrinter.");}
    }

    /**
     * Override an entire TimeSeries file with given csvString.
     * @param csvString The CSV string that will override the original file.
     * @param type A {@link com.csc301group36.Covid19API.Entities.TimeSeriesRequestType} that defines which
     * file it should apply the change to.
     * */
    public boolean overrideTimeSeriesFile(String csvString, TimeSeriesRequestType type) throws InternalError{
        List<CSVRecord> records = getRecords(csvString);
        if(csvFormatChecker.isValidTimeSeriesOverride(records)){
            dbManager.writeToTimeSeriesFile(csvString, type);
            return true;
        }
        return false;
    }
    /**
     * Update a TimeSeries file.
     * <p>A file will be updated by two ways: </p>
     * <p>1. Append: Any record that does not match all records in old file will be appended.</p>
     * <p>2. Replace: Any record that matches a record in the old file will replace the old record.</p>
     * @param csvString The CSV string containing records that will be inserted into the file.
     * @param type A {@link com.csc301group36.Covid19API.Entities.TimeSeriesRequestType} that defines which
     * file it should apply the change to.
     * */
    //TODO: need to deal with False return value when format is incorrect.
    public boolean updateTimeSeriesFile(String csvString, TimeSeriesRequestType type) throws InternalError{
        List<CSVRecord> newRecords = getRecords(csvString);
        Collection<String> headers = getHeaders(newRecords);
        if(csvFormatChecker.isValidTimeSeriesUpdate(newRecords, headers)){
            List<CSVRecord> oldRecords = getRecords(dbManager.getTimeSeriesFile(type));
            // Insert all oldRecords first
            List<CSVRecord> mergedRecords = new ArrayList<>(oldRecords);
            try {
                // merge records by replacing oldRecords
                for(CSVRecord newRecord : newRecords){
                    boolean replaced = false;
                    for(CSVRecord oldRecord : oldRecords){
                        if(newRecord.get("Province/State").equals(oldRecord.get("Province/State")) &&
                                newRecord.get("Country/Region").equals(oldRecord.get("Country/Region")) &&
                                newRecord.get("Lat").equals(oldRecord.get("Lat")) &&
                                newRecord.get("Long").equals(oldRecord.get("Long"))
                        ){
                            mergedRecords.set(mergedRecords.indexOf(oldRecord), newRecord);
                            replaced = true;
                            break;
                        }
                    }
                    if(!replaced) mergedRecords.add(newRecord);
                }
                // Write merged records into StringWriter
                StringWriter writer = new StringWriter();
                CSVPrinter printer = getPrinter(headers, writer);
                printer.printRecords(mergedRecords);
                dbManager.writeToTimeSeriesFile(writer.toString(), type);
            }catch (Exception e) {throw new InternalError("Database error(Failed to write to file.)");}
            return true;
        }
        return false;
    }

    /**
     * Update a dailyReports file.
     * <p>A file will be updated by two ways: </p>
     * <p>1. Append: Any record that does not match all records in old file will be appended.</p>
     * <p>2. Replace: Any record that matches a record in the old file will replace the old record.</p>
     * @param csvString The CSV string containing records that will be inserted into the file.
     * @param date A date that defines the file that this csvString should apply change to.
     * */
    public boolean updateDailyReportsFile(String csvString, String date) throws InternalError{
        List<CSVRecord> newRecords = getRecords(csvString);
        Collection<String> headers = getHeaders(newRecords);
        if(csvFormatChecker.isValidDailyReports(newRecords)){
            List<CSVRecord> oldRecords = getRecords(dbManager.getDailyFile(date));
            // Insert all oldRecords first
            List<CSVRecord> mergedRecords = new ArrayList<>(oldRecords);
            try {
                // merge records by replacing oldRecords
                for(CSVRecord newRecord : newRecords){
                    boolean replaced = false;
                    for(CSVRecord oldRecord : oldRecords){
                        if(newRecord.get("FIPS").equals(oldRecord.get("FIPS")) &&
                                newRecord.get("Admin2").equals(oldRecord.get("Admin2")) &&
                                newRecord.get("Province_State").equals(oldRecord.get("Province_State")) &&
                                newRecord.get("Country_Region").equals(oldRecord.get("Country_Region")) &&
                                newRecord.get("Combined_Key").equals(oldRecord.get("Combined_Key"))
                        ){
                            mergedRecords.set(mergedRecords.indexOf(oldRecord), newRecord);
                            replaced = true;
                            break;
                        }
                    }
                    if(!replaced) mergedRecords.add(newRecord);
                }
                // Write merged records into StringWriter
                StringWriter writer = new StringWriter();
                CSVPrinter printer = getPrinter(headers, writer);
                printer.printRecords(mergedRecords);
                dbManager.writeToDailyReportsFile(writer.toString(), date);
            }catch (IOException ioe) {
                throw new InternalError("Database error(Failed to write to file.)");
            }catch (Exception e){
                throw new InternalError("Failed to load fields.");
            }
        }
        return false;
    }

    /**
     * Query data from some file in the database.
     * @param conditions Query conditions.
     * @return a list of records that satisfied the conditions.
     * */
    public List<CSVRecord> query(Conditions conditions) throws InternalError {
        if(conditions.getType() == DBType.TimeSeries){
            return getTimeSeriesQueryResult(conditions);
        }
        else{
            return getDailyReportQueryResult(conditions);
        }
    }

    /**
     * Get all records from a file.
     * @param f A CSV file.
     * */
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

    /**
     * Get all records from string.
     * @param csvString A string with CSV format.
     * */
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

    /**
     * Get headers(fields) from records.
     * @param records records from the same csv source.
     * */
    public Collection<String> getHeaders(List<CSVRecord> records){
        if(!records.isEmpty()){
            CSVRecord record = records.get(0);
            return record.getParser().getHeaderNames();
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
