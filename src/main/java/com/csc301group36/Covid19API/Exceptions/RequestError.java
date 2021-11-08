package com.csc301group36.Covid19API.Exceptions;

public class RequestError extends Exception{
    public RequestError(String msg){
        super(msg);
    }
}
