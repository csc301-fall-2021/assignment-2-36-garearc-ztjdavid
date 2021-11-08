package com.csc301group36.Covid19API.Utils;

import com.csc301group36.Covid19API.Entities.TimeSeriesRequestType;
import com.csc301group36.Covid19API.Exceptions.RequestError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
public class DBManager {
    private final HashMap<String, String> dateToDaily = new HashMap<>();
    private final String timeSeriesPath = "/timeSeries";
    private final String dailyReportsPath = "/dailyReports";

    @Autowired
    private CSVFormatChecker csvFormatChecker;

    private DBManager(){
        initFolders();
    }

    public File uploadTimeSeriesFile(TimeSeriesRequestType type, String content) throws RequestError {
        File f = new File(getFilePath(timeSeriesPath, type.fileName));
        try{
            f.createNewFile();
            FileWriter writer = new FileWriter(f);
            writer.write(content);
            writer.close();
        }catch (Exception e){ throw new RequestError("We have encountered a server issue, please try later.");}
        if(csvFormatChecker.isValidTimeSeries(f)) {
            return f;
        }
        throw new RequestError("Invalid csv format. Please make sure you upload string with correct csv format.");
    }

    public File uploadDailyReportFile(String date, String content) throws RequestError{
        File f = new File(getFilePath(dailyReportsPath, date));
        try{
            f.createNewFile();
            FileWriter writer = new FileWriter(f);
            writer.write(content);
            writer.close();
        }catch (Exception e){throw new RequestError("We have encountered a server issue, please try later.");}
        if(csvFormatChecker.isValidDailyReports(f)){
            dateToDaily.put(date, getFilePath(dailyReportsPath, date));
            return f;
        }
        f.delete();
        throw new RequestError("Invalid csv format. Please make sure the uploaded string has correct csv format.");
    }

    public File getTimeSeriesFile(TimeSeriesRequestType type) throws RequestError{
        File f = new File(getFilePath(timeSeriesPath, type.fileName));
        if(!f.exists()) {
            throw new RequestError("We do not have corresponding datebase now. Please upload csv data first.");
        }
        return f;
    }


    public File getDailyFile(String date) throws RequestError{
        if(dateToDaily.containsKey(date)){
            File f = new File(getFilePath(dailyReportsPath, dateToDaily.get(date)));
            if(!f.exists()) {
                throw new RequestError("We do not have corresponding datebase now. Please upload csv data first.");
            }
            return f;
        }
        throw new RequestError("We do not have corresponding datebase now. Please upload csv data first.");
    }

    public List<String> getDailyFileDates(){
        return new ArrayList<>(dateToDaily.keySet());
    }

    private void initFolders(){
        new File(timeSeriesPath).mkdirs();
        new File(dailyReportsPath).mkdirs();
    }

    private String getFilePath(String basePath, String fileName){
        return basePath + "/" + fileName + ".csv";
    }
}
