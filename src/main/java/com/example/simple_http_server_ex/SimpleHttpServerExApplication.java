package com.example.simple_http_server_ex;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Stack;

@SpringBootApplication
@RestController
public class SimpleHttpServerExApplication {

    private static Stack<Integer> argsStack;
    private static int argsStackSize;

    public static void main(String[] args) {

        argsStack = new Stack<>();
        argsStackSize = 0;

        SpringApplication.run(SimpleHttpServerExApplication.class, args);
    }

    @PostMapping(value = "/independent/calculate",
    consumes = {MediaType.APPLICATION_JSON_VALUE},
    produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResultResp> independentCalculation(@RequestBody CalcReq calcReq) {

        int statusCode = 200;
        ResultResp resultResp;

        System.out.println(calcReq);
        CalcResult calcResult = CalcUtill.performOp(calcReq.getOperation(), calcReq.getArguments());

        if (!calcResult.isSucceeded()) {
            statusCode = 409;
            resultResp = new ResultResp(calcResult.getDetails());
        } else {
            resultResp = new ResultResp(calcResult.getValue());
        }

        System.out.println(resultResp);
        return ResponseEntity.status(statusCode).body(resultResp);
    }

    @GetMapping("/stack/size")
    public int getStackSize () {
        return argsStackSize;
    }

    @PutMapping(value = "/stack/arguments",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public int addArguments(@RequestBody CalcReq argReq) {

        for (Integer arg : argReq.getArguments()) {
            argsStack.push(arg);
            argsStackSize++;
        }

        return argsStackSize;
    }


    //@RequestParam(value = "myName", defaultValue = "World")

}
