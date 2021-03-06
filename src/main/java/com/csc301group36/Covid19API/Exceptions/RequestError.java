package com.csc301group36.Covid19API.Exceptions;


import lombok.Getter;

@Getter
public class RequestError extends Exception{
    private int code = 400;
    private final String description;
    public RequestError(String msg) {
        super(msg);
        description = msg;
    }
    public RequestError(String msg, int code) {
        super(msg);
        description = msg;
        this.code = code;
    }
}
