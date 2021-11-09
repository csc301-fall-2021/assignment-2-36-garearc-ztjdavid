package com.csc301group36.Covid19API.Exceptions;

public class RecordGetError extends InternalError {

    public RecordGetError(String colName) {
        super("Attribute cannot be found: " + colName +
                ". Please check your request or csv file.");
    }
    public RecordGetError(String colName, int code) {
        super("Attribute cannot be found: " + colName +
                ". Please check your request or csv file.", code);
    }
}
