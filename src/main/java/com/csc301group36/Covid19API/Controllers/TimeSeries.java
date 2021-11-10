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
import java.util.Map;

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
    public Response overWrite(@RequestBody String data, @PathVariable("type") String type) throws RequestError {
        Response response = new Response();
        try{
            if(csvManager.overrideTimeSeriesFile(data, dataService.parseTimeRequestType(type))){
                response.setCompleted(true);
                response.setDescription("OverWrite executed successfully");
            } else{
                response.setCompleted(false);
                response.setDescription("Failed to execute OverWrite");
            }
        }catch (InternalError ie){
            response.setCompleted(false);
            response.setDescription(ie.getDescription());
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

    @PostMapping(path = "/query/csv/{type}")
    public ResponseEntity<String> queryCsvData(@PathVariable("type") String type
        ,@RequestParam(name = "country", required = false) String country,
        @RequestParam(name = "state", required = false)String state,
        @RequestParam(name = "combinedKeys", required = false) String combinedKeys,
        @RequestParam(name = "startDate", required = false) String startDate,
        @RequestParam(name = "endDate", required = false) String endDate) throws InternalError, RequestError{
            ReqBody reqBody = new ReqBody();
            reqBody.setCountry(country);
            reqBody.setState(state);
            reqBody.setEndDate(endDate);
            reqBody.setCombinedKeys(combinedKeys);
            reqBody.setStartDate(startDate);
        return dataService.getCsvData(reqBody, type, DBType.TimeSeries);
    }

    @GetMapping(path = "/query/json/{type}")
    public List<Map<String, String>> queryJsonData(@RequestParam(name = "country", required = false) String country,
                                                   @RequestParam(name = "state", required = false)String state,
                                                   @RequestParam(name = "combinedKeys", required = false) String combinedKeys,
                                                   @RequestParam(name = "startDate", required = false) String startDate,
                                                   @RequestParam(name = "endDate", required = false) String endDate,
                                                   @PathVariable("type") String type) throws InternalError, RequestError{
        ReqBody reqBody = new ReqBody();
        reqBody.setCountry(country);
        reqBody.setState(state);
        reqBody.setEndDate(endDate);
        reqBody.setCombinedKeys(combinedKeys);
        reqBody.setStartDate(startDate);
        Conditions conditions = dataService.processInput(reqBody, type, DBType.TimeSeries);
        List<CSVRecord> records = csvManager.query(conditions);
        return queryParser.parseJSON(records);
    }


}


