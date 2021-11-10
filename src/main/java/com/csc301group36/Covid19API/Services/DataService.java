package com.csc301group36.Covid19API.Services;

import com.csc301group36.Covid19API.Controllers.TimeSeries;
import com.csc301group36.Covid19API.Entities.*;
import com.csc301group36.Covid19API.Exceptions.InternalError;
import com.csc301group36.Covid19API.Exceptions.InvalidDataTypeError;
import com.csc301group36.Covid19API.Utils.CSVManager;
import com.csc301group36.Covid19API.Utils.QueryParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import com.csc301group36.Covid19API.Exceptions.RequestError;
import com.csc301group36.Covid19API.Utils.DateUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataService {
    @Autowired
    DateUtils dateUtils;
    @Autowired
    CSVManager csvManager;
    @Autowired
    QueryParser queryParser;


    public Conditions processInput(ReqBody reqBody, String type, DBType dbType) throws RequestError {
        Conditions conditions = new Conditions();
        conditions.setCountry(reqBody.getCountry());
        conditions.setState(reqBody.getState());
        conditions.setCombinedKeys(reqBody.getCombinedKeys());
        if ((!dateUtils.isValidDate(reqBody.getEndDate(), dbType)) ||
                (!dateUtils.isValidDate(reqBody.getStartDate(), dbType))) {
            throw new RequestError("Either Start Date: " + reqBody.getStartDate() + " " +
                    "or End Date: " + reqBody.getEndDate() + " is incorrectly formatted.");
        }
        conditions.setEndDate(reqBody.getEndDate());
        conditions.setStartDate(reqBody.getStartDate());
        conditions.setType(dbType);
        if(dbType == DBType.TimeSeries){
            conditions.setTimeSeriesRequestType(parseTimeRequestType(type));
        }else{
            conditions.setDailyReportRequestType(parseDailyRequestType(type));
        }

        return conditions;
    }

    public TimeSeriesRequestType parseTimeRequestType(String type) throws RequestError {
        switch (type) {
            case "death":
                return TimeSeriesRequestType.death;
            case "confirmed":
                return TimeSeriesRequestType.confirmed;
            case "active":
                return TimeSeriesRequestType.active;
            case "recovered":
                return TimeSeriesRequestType.recovered;
            default:
                throw new RequestError("Input type: " + type + " is not identified");
        }
    }

    public DailyReportRequestType parseDailyRequestType(String type) throws RequestError {
        switch (type) {
            case "death":
                return DailyReportRequestType.death;
            case "confirmed":
                return DailyReportRequestType.confirmed;
            case "active":
                return DailyReportRequestType.active;
            case "recovered":
                return DailyReportRequestType.recovered;
            default:
                throw new RequestError("Input type: " + type + " is not identified");
        }

    }

    public void validateTime(String date) throws RequestError {
        if (!dateUtils.isValidDate(date, DBType.DailyReports)) {
            throw new RequestError("Date: " + date + " is incorrectly formatted!");
        }
    }

    public ResponseEntity<String> getCsvData(ReqBody reqBody, String type, DBType dbType)throws InternalError, RequestError{
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_PLAIN);
        Conditions conditions = processInput(reqBody, type, dbType);
        List<CSVRecord> records = csvManager.query(conditions);
        String queriedData = queryParser.parseCSV(records);
        return new ResponseEntity<>(queriedData, header, HttpStatus.OK);
    }



}
