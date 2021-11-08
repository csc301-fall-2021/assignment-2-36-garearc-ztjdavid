package com.csc301group36.Covid19API.Exceptions;

public class RecordGetError extends RequestError{

    public RecordGetError(String colName) {
        super("Attribute cannot be found: " + colName);
    }
}
