package com.csc301group36.Covid19API.Controllers;

import com.csc301group36.Covid19API.Entities.Conditions;
import com.csc301group36.Covid19API.Entities.ExceptionResponse;
import com.csc301group36.Covid19API.Entities.ReqBody;
import com.csc301group36.Covid19API.Utils.CSVManager;
import com.csc301group36.Covid19API.Exceptions.InternalError;
import com.csc301group36.Covid19API.Exceptions.RequestError;
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
@RequestMapping("/api/csv/timeSeries")
public class TimeSeriesCsv {
    @Autowired
    ControllerExceptionHandler controllerExceptionHandler;
    @Autowired
    CSVManager csvManager;

    @GetMapping(path = "/overwrite/{type}")
    public ResponseEntity<String> overWrite(@RequestBody String data, @PathVariable("type") String type) throws InternalError, RequestError {
        //parse the input data and store them in respective entities.
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_PLAIN);
        Conditions conditions = new Conditions();
        ReqBody reqBody = new ReqBody();
        // TODO
        // parseInput
        parseInput(data, type, conditions, reqBody);
        //call methods to overwrite data in existing database
        try {
            csvManager.overrideTimeSeriesFile(data, conditions.getTimeSeriesRequestType());
        } catch (InternalError internalError){
            ResponseEntity<ExceptionResponse> errFound = controllerExceptionHandler.handleInternalError(internalError);
            return new ResponseEntity<>(Objects.requireNonNull(errFound.getBody()).getDescription(),header, errFound.getStatusCode());
        }
        return new ResponseEntity<>("Overwrite Successful!",header, HttpStatus.OK);
    }

    @GetMapping(path = "/update/{type}")
    public ResponseEntity<String> update(@RequestBody String data, @PathVariable("type") String type) throws InternalError, RequestError {
        //parse the input data and store them in respective entities.
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_PLAIN);
        Conditions conditions = new Conditions();
        ReqBody reqBody = new ReqBody();
        // TODO
        // parse the input
        parseInput(data, type, conditions, reqBody);
        try {
            csvManager.updateTimeSeriesFile(data, conditions.getTimeSeriesRequestType());
        } catch (InternalError internalError) {
            ResponseEntity<ExceptionResponse> errFound = controllerExceptionHandler.handleInternalError(internalError);
            return new ResponseEntity<>(Objects.requireNonNull(errFound.getBody()).getDescription(),header, errFound.getStatusCode());
        }

        return new ResponseEntity<>("Update Successful!",header, HttpStatus.OK);
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
            controllerExceptionHandler.handleInternalError(internalError);
        }

        String queriedData = "";
        //TODO:
        //parse records into the final string wanted.

        return new ResponseEntity<>(queriedData,header, HttpStatus.OK);

    }


    private void parseInput(String data, String type, Conditions conditions, ReqBody reqBody) {
//        todo
//        parse the input from the user
//
//        conditions.setType(DBType.TimeSeries);
//        try {
//            switch (type) {
//                case "death":
//                    conditions.setTimeSeriesRequestType(TimeSeriesRequestType.death);
//                    break;
//                case "confirmed":
//                    conditions.setTimeSeriesRequestType(TimeSeriesRequestType.confirmed);
//                    break;
//                case "active":
//                    conditions.setTimeSeriesRequestType(TimeSeriesRequestType.active);
//                    break;
//                case "recovered":
//                    conditions.setTimeSeriesRequestType(TimeSeriesRequestType.recovered);
//                    break;
//                default:
//                    throw new RequestError("Input type is not identified");
//            }
//        } catch (RequestError requestError) {
//            controllerExceptionHandler.handleRequestError(requestError);
//        }
    }



}


