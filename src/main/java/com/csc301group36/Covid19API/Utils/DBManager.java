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

    private DBManager(){
        initFolders();
    }

    public void writeToTimeSeriesFile(String content, TimeSeriesRequestType type) throws InternalError{
        writeContentToFile(content, getFilePath(timeSeriesDirName, type.fileName));
    }

    public void writeToDailyReportsFile(String content, String date) throws InternalError{
        String path = getFilePath(dailyReportsDirName, dateToDaily.get(date));
        dateToDaily.put(date, path);
        writeContentToFile(content, path);
    }

    public File getTimeSeriesFile(TimeSeriesRequestType type) throws InternalError {
        File f = new File(getFilePath(timeSeriesDirName, type.fileName));
        if(!f.exists()) {
            throw new InternalError("We do not have corresponding database now. Please upload csv data first.");
        }
        return f;
    }


    public File getDailyFile(String date) throws InternalError {
        if(dateToDaily.containsKey(date)){
            File f = new File(getFilePath(dailyReportsDirName, dateToDaily.get(date)));
            if(!f.exists()) {
                throw new InternalError("We do not have corresponding database now. Please upload csv data first.");
            }
            return f;
        }
        throw new InternalError("We do not have data on that date now. Please upload csv data first.");
    }

    public List<String> getDailyFileDates(){
        return new ArrayList<>(dateToDaily.keySet());
    }

    public void writeContentToFile(String content, String filepath) throws InternalError{
        try{
            File f = new File(filepath);
            f.createNewFile();
            FileWriter writer = new FileWriter(f);
            writer.write(content);
            writer.flush();
            writer.close();
        }catch (Exception e){
            throw new InternalError("Server cannot save your data, please try again.");
        }
    }

    // Helpers

    private void initFolders(){
        new File(timeSeriesDirName).mkdirs();
        new File(dailyReportsDirName).mkdirs();
    }

    private String getFilePath(String dirName, String fileName){
        return "/" + dirName + "/" + fileName + ".csv";
    }
}
