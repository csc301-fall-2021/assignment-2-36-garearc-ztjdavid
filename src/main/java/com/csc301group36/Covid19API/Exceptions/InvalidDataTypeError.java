package com.csc301group36.Covid19API.Exceptions;

public class InvalidDataTypeError extends InternalError {

    public InvalidDataTypeError(String record, String field) {
        super("We found an invalid data(row) in the database: " +
                record + " In field: "+ field);
    }
    public InvalidDataTypeError(String record, String field, int code) {
        super("We found an invalid data(row) in the database: " +
                record + " In field: "+ field, code);
    }
}
