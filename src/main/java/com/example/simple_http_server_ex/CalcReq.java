package com.example.simple_http_server_ex;

import java.util.List;

public class CalcReq {
    private List<Integer> arguments;
    private String operation;

    public CalcReq(List<Integer> arguments, String operation) {
        this.arguments = arguments;
        this.operation = operation;
    }

    public List<Integer> getArguments() {
        return arguments;
    }

    public String getOperation() {
        return operation;
    }

}
