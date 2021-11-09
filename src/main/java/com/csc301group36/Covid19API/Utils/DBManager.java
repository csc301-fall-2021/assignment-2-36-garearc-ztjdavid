package com.csc301group36.Covid19API.Utils;

import com.csc301group36.Covid19API.Entities.TimeSeriesRequestType;
import com.csc301group36.Covid19API.Exceptions.InternalError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
public class DBManager {
    private final HashMap<String, String> dateToDaily = new HashMap<>();
    private final String timeSeriesDirName = "timeSeries";
    private final String dailyReportsDirName = "dailyReports";

    @Autowired
    private CSVFormatChecker csvFormatChecker;

    private DBManager(){
        initFolders();
    }

    public File uploadTimeSeriesFile(TimeSeriesRequestType type, String content) throws InternalError {
        File f = new File(getFilePath(timeSeriesDirName, type.fileName));
        try{
            f.createNewFile();
            FileWriter writer = new FileWriter(f);
            writer.write(content);
            writer.close();
        }catch (Exception e){ throw new InternalError("We have encountered a server issue, please try later.");}
        if(csvFormatChecker.isValidTimeSeries(f)) {
            return f;
        }
        throw new InternalError("Invalid csv format. Please make sure you upload string with correct csv format.");
    }

    public File uploadDailyReportFile(String date, String content) throws InternalError {
        File f = new File(getFilePath(dailyReportsDirName, date));
        try{
            f.createNewFile();
            FileWriter writer = new FileWriter(f);
            writer.write(content);
            writer.close();
        }catch (Exception e){throw new InternalError("We have encountered a server issue, please try later.");}
        if(csvFormatChecker.isValidDailyReports(f)){
            dateToDaily.put(date, getFilePath(dailyReportsDirName, date));
            return f;
        }
        f.delete();
        throw new InternalError("Invalid csv format. Please make sure the uploaded string has correct csv format.");
    }

    public File getTimeSeriesFile(TimeSeriesRequestType type) throws InternalError {
        File f = new File(getFilePath(timeSeriesDirName, type.fileName));
        if(!f.exists()) {
            throw new InternalError("We do not have corresponding datebase now. Please upload csv data first.");
        }
        return f;
    }


    public File getDailyFile(String date) throws InternalError {
        if(dateToDaily.containsKey(date)){
            File f = new File(getFilePath(dailyReportsDirName, dateToDaily.get(date)));
            if(!f.exists()) {
                throw new InternalError("We do not have corresponding datebase now. Please upload csv data first.");
            }
            return f;
        }
        throw new InternalError("We do not have corresponding datebase now. Please upload csv data first.");
    }

    public List<String> getDailyFileDates(){
        return new ArrayList<>(dateToDaily.keySet());
    }

    private void initFolders(){
        new File(timeSeriesDirName).mkdirs();
        new File(dailyReportsDirName).mkdirs();
    }

    private String getFilePath(String dirName, String fileName){
        return "/" + dirName + "/" + fileName + ".csv";
    }
}
