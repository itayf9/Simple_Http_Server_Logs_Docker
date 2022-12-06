package com.example.utillity;

public class CalcResult {
    private boolean isSucceeded;
    private String details;
    private int value;

    public CalcResult(boolean isSucceeded, String details, int value) {
        this.isSucceeded = isSucceeded;
        this.details = details;
        this.value = value;
    }

    public boolean isSucceeded() {
        return isSucceeded;
    }

    public String getDetails() {
        return details;
    }

    public int getValue() {
        return value;
    }
}
