package com.csc301group36.Covid19API.Exceptions;

import lombok.Getter;

@Getter
public class InternalError extends Exception{
    private final String description;
    private int code = 404;
    public InternalError(String msg){
        super(msg);
        description = msg;
    }
    public InternalError(String msg, int code){
        super(msg);
        description = msg;
        this.code = code;
    }
}
