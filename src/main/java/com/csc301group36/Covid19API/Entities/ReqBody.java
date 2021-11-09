package com.csc301group36.Covid19API.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ReqBody {
    @JsonProperty(value = "country")
    private String country;
    @JsonProperty(value = "state")
    private String state;
    @JsonProperty(value = "combinedKeys")
    private String combinedKeys;
    @JsonProperty(value = "startDate")
    private String startDate;
    @JsonProperty(value = "endDate")
    private String endDate;
}
