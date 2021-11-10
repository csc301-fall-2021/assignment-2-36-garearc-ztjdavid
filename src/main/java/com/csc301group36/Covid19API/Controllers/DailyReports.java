package com.csc301group36.Covid19API.Controllers;

import com.csc301group36.Covid19API.Entities.*;
import com.csc301group36.Covid19API.Exceptions.InternalError;
import com.csc301group36.Covid19API.Exceptions.RequestError;
import com.csc301group36.Covid19API.Services.DataService;
import com.csc301group36.Covid19API.Utils.CSVManager;
import com.csc301group36.Covid19API.Utils.QueryParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin(origins = "*", maxAge = 10000)
@RequestMapping("/api/DailyReports")
public class DailyReports {
    @Autowired
    CSVManager csvManager;
    @Autowired
    DataService dataService;
    @Autowired
    QueryParser queryParser;

    @PostMapping(path = "/upload/{date}")
    public Response upload(@RequestBody String data, @PathVariable("date") String date) throws InternalError, RequestError {
        Response response = new Response();
        dataService.validateTime(date);
        if(csvManager.updateDailyReportsFile(data, date)){
            response.setCompleted(true);
            response.setDescription("Upload successfully executed");
        }else{
            response.setCompleted(false);
            response.setDescription("Upload failed;");
        }
        return response;
    }

    @GetMapping(path = "/query/csv/{type}")
    public ResponseEntity<String> queryCsvData(@PathVariable("type") String type,
                                               @RequestParam(name = "country", required = false) String country,
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
        return dataService.getCsvData(reqBody, type, DBType.DailyReports);
    }

    @GetMapping (path = "/query/json/{type}", produces = "application/json")
    public List<Map<String, String>> queryJsonData(@PathVariable("type") String type,
                                                   @RequestParam(name = "country", required = false) String country,
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
        Conditions conditions = dataService.processInput(reqBody, type, DBType.DailyReports);
        List<CSVRecord> records = csvManager.query(conditions);
        return queryParser.parseJSON(records);
    }



}


