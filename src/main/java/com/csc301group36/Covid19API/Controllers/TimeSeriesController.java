package com.csc301group36.Covid19API.Controllers;

import com.csc301group36.Covid19API.Entities.Conditions;
import com.csc301group36.Covid19API.Entities.TimeSeriesRequestType;
import com.csc301group36.Covid19API.Exceptions.InternalError;
import com.csc301group36.Covid19API.Exceptions.RequestError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 10000)
@RequestMapping("/api/timeSeries")
public class TimeSeriesController {
    @GetMapping("/csv/test")
    public ResponseEntity<String> test() throws InternalError, RequestError {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity<>("sadge", header, HttpStatus.OK);
    }
}
