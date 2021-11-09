package com.csc301group36.Covid19API.Exceptions;

public class InvalidCSVFormatError extends InternalError{

    public InvalidCSVFormatError(String filename) {
        super("The csv file " + filename + " has invalid format, " +
                "please check and update this csv file.");
    }

    public InvalidCSVFormatError(String filename, int code) {
        super("The csv file " + filename + " has invalid format, " +
                "please check and update this csv file.", code);
    }
}
