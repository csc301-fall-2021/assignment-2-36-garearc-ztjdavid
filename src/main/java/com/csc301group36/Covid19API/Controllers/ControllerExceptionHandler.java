package com.csc301group36.Covid19API.Controllers;

import com.csc301group36.Covid19API.Entities.ExceptionResponse;
import com.csc301group36.Covid19API.Exceptions.InternalError;
import com.csc301group36.Covid19API.Exceptions.RequestError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(InternalError.class)
    public ResponseEntity<ExceptionResponse> handleInternalError(InternalError e){
        ExceptionResponse er = new ExceptionResponse(e.getCode(), e.getDescription());
        return new ResponseEntity<>(er, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RequestError.class)
    public ResponseEntity<ExceptionResponse> handleRequestError(RequestError e){
        ExceptionResponse er = new ExceptionResponse(e.getCode(), e.getDescription());
        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }


}
