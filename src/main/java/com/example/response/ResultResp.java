package com.example.response;

public class ResultResp {
    private int result;
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
