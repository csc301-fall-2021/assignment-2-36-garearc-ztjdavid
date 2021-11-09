package com.csc301group36.Covid19API.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ReqBody {
    private String country;
    private String state;
    private String combinedKeys;
    private String startDate;
    private String endDate;
}
