package com.example.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultResp {

    @JsonProperty("result")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer result;

    @JsonProperty("error-message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage;

    public ResultResp(int result) {
        this.result = result;
    }

    public ResultResp(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
