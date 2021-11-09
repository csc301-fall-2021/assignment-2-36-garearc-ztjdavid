package com.csc301group36.Covid19API.Controllers;

import com.csc301group36.Covid19API.Entities.Conditions;
import com.csc301group36.Covid19API.Entities.ExceptionResponse;
import com.csc301group36.Covid19API.Entities.ReqBody;
import com.csc301group36.Covid19API.Exceptions.InternalError;
import com.csc301group36.Covid19API.Exceptions.RequestError;
import com.csc301group36.Covid19API.Utils.CSVManager;
import com.csc301group36.Covid19API.Utils.DBManager;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


@RestController
@CrossOrigin(origins = "*", maxAge = 10000)
@RequestMapping("/api/csv/DailyReports")
public class DailyReportsCsv {
    @Autowired
    CSVManager csvManager;
    @Autowired
    DBManager dbManager;
    @Autowired
    ControllerExceptionHandler controllerExceptionHandler;

    @GetMapping(path = "/upload/{date}")
    public ResponseEntity<String> upload(@RequestBody String data, @PathVariable("date") String date) throws InternalError, RequestError {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_PLAIN);
        Conditions conditions = new Conditions();
        ReqBody reqBody = new ReqBody();
        // TODO
        // parseInput
        parseInput(data, date, conditions, reqBody);
        try {
            dbManager.writeToDailyReportsFile(data, date);
        } catch (InternalError internalError) {
            ResponseEntity<ExceptionResponse> errFound = controllerExceptionHandler.handleInternalError(internalError);
            return new ResponseEntity<>(Objects.requireNonNull(errFound.getBody()).getDescription(), header, errFound.getStatusCode());
        }
        return new ResponseEntity<>("Upload successful!", header, HttpStatus.OK);
    }

    @PostMapping(path = "/query/{type}")
    public ResponseEntity<String> queryData(@RequestBody String data, @PathVariable("type") String type) throws InternalError, RequestError{
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_PLAIN);
        Conditions conditions = new Conditions();
        ReqBody reqBody = new ReqBody();
        parseInput(data, type, conditions, reqBody);
        try {
            List<CSVRecord> records = csvManager.query(conditions);
        } catch ( InternalError internalError){
            ResponseEntity<ExceptionResponse> errFound = controllerExceptionHandler.handleInternalError(internalError);
            return new ResponseEntity<>(Objects.requireNonNull(errFound.getBody()).getDescription(), header, errFound.getStatusCode());
        }

        String queriedData = "";
        //TODO:
        //parse records into the final string wanted.

        return new ResponseEntity<>(queriedData, header, HttpStatus.OK);

    }


    private void parseInput(String data, String type, Conditions conditions, ReqBody reqBody) {
        //TODO
    }



}


