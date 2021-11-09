package com.csc301group36.Covid19API.Exceptions;

public class InvalidDataTypeError extends InternalError {

    public InvalidDataTypeError(String record, String filename) {
        super("We found an invalid data(row) in the database: " +
                record + "\n" + "In database: "+ filename);
    }
}
