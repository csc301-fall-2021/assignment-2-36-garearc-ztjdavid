package com.csc301group36.Covid19API.Entities;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class Response {
    private List<Map<String, String>> queryResult;
}
