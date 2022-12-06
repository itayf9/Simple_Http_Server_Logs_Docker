package com.example.simple_http_server_ex;

import java.util.List;

public class ArgReq {

    private List<Integer> arguments;

    public ArgReq(List<Integer> arguments) {
        this.arguments = arguments;
    }

    public List<Integer> getArguments() {
        return arguments;
    }
}
