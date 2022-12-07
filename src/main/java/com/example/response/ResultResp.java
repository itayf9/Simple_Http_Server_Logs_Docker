package com.example.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultResp {

    @JsonProperty("result")
    private int result;

    @JsonProperty("error-message")
    private String errorMessage;

    public ResultResp(int result) {
        this.result = result;
        this.errorMessage = "";
    }

    public ResultResp(String errorMessage) {
        this.result = 0;
        this.errorMessage = errorMessage;
    }

    public int getResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
