package com.example.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ArgReq {

    @JsonProperty("arguments")
    private List<Integer> arguments;

    public ArgReq() {
    }

    public ArgReq(List<Integer> arguments) {
        this.arguments = arguments;
    }

    public List<Integer> getArguments() {
        return arguments;
    }
}
