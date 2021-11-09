package com.csc301group36.Covid19API.Controllers;

import com.csc301group36.Covid19API.Entities.*;
import com.csc301group36.Covid19API.Utils.CSVManager;
import com.csc301group36.Covid19API.Exceptions.InternalError;
import com.csc301group36.Covid19API.Exceptions.RequestError;
import com.csc301group36.Covid19API.Services.DataService;
import com.csc301group36.Covid19API.Utils.QueryParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 10000)
@RequestMapping("/api/timeSeries")
public class TimeSeries {
    @Autowired
    CSVManager csvManager;
    @Autowired
    DataService dataService;
    @Autowired
    QueryParser queryParser;

    @PostMapping(path = "/overwrite/{type}")
    public Response overWrite(@RequestBody String data, @PathVariable("type") String type) throws InternalError, RequestError {
        Response response = new Response();
        if(csvManager.overrideTimeSeriesFile(data, dataService.parseTimeRequestType(type))){
            response.setCompleted(true);
            response.setDescription("OverWrite executed successfully");
        } else{
            response.setCompleted(false);
            response.setDescription("Failed to execute OverWrite");
        }
        return response;
    }

    @PostMapping(path = "/update/{type}")
    public Response update(@RequestBody String data, @PathVariable("type") String type) throws InternalError, RequestError {
        Response response = new Response();
        if(csvManager.updateTimeSeriesFile(data, dataService.parseTimeRequestType(type))){
            response.setCompleted(true);
            response.setDescription("update executed successfully");
        } else{
            response.setCompleted(false);
            response.setDescription("Failed to execute update");
        }
        return response;
    }

    @GetMapping(path = "/query/csv/{type}")
    public ResponseEntity<String> queryData(@RequestBody ReqBody reqBody, @PathVariable("type") String type) throws InternalError, RequestError{
        return dataService.getCsvData(reqBody, type);
    }

    @GetMapping(path = "/query/json/{type}")
    public QueryResponse queryJsonData(@RequestBody ReqBody reqBody, @PathVariable("type") String type) throws InternalError, RequestError{
        Conditions conditions = dataService.processInput(reqBody, type);
        List<CSVRecord> records = csvManager.query(conditions);
        return queryParser.parseJSON(records);
    }


}


