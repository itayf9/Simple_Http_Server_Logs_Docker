package com.example.simple_http_server_ex.controller;

import com.example.request.ArgReq;
import com.example.request.CalcReq;
import com.example.utillity.CalcError;
import com.example.utillity.CalcResult;
import com.example.utillity.CalcUtill;
import com.example.response.ResultResp;
import com.example.utillity.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;



@RestController
public class CalcController {

    private Stack<Integer> argsStack;
    private int argsStackSize;

    public CalcController() {
        argsStack = new Stack<>();
        argsStackSize = 0;
    }

    @PostMapping(value = "/independent/calculate",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResultResp> independentCalculation(@RequestBody CalcReq calcReq) {

        HttpStatus statusCode = HttpStatus.OK;
        ResultResp resultResp;

        System.out.println(calcReq);
        CalcResult calcResult = CalcUtill.performOp(calcReq.getOperation(), calcReq.getArguments());

        if (!calcResult.isSucceeded()) {
            statusCode = HttpStatus.CONFLICT;
            resultResp = new ResultResp(calcResult.getDetails());
        } else {
            resultResp = new ResultResp(calcResult.getValue());
        }

        System.out.println(resultResp);
        return ResponseEntity.status(statusCode).body(resultResp);
    }

    @GetMapping(value = "/stack/size",
    produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResultResp> getStackSize () {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResultResp(argsStackSize));
    }

    @PutMapping(value = "/stack/arguments",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResultResp> addArguments(@RequestBody ArgReq argReq) {

        for (Integer arg : argReq.getArguments()) {
            argsStack.push(arg);
            argsStackSize++;
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResultResp(argsStackSize));
    }

    @GetMapping(value = "/stack/operate",
    produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResultResp> performOperation(@RequestParam(value = "operation", defaultValue = "undefined") String operationStr) {

        HttpStatus statusCode = HttpStatus.OK;
        ResultResp resultResp;

        Operation operation = Operation.getMatchOperation(operationStr);

        if (operation.equals(Operation.UNDEFINED)) {
            statusCode = HttpStatus.CONFLICT;
            resultResp = new ResultResp(CalcError.UNKNOWN_OPERATION + operationStr);
            return ResponseEntity.status(statusCode).body(resultResp);
        }

        // the operation is valid
        int numOfNeededArguments = operation.numOfNeededArgs();

        if (argsStackSize < numOfNeededArguments) {
            statusCode = HttpStatus.CONFLICT;
            resultResp = new ResultResp(String.format(CalcError.NOT_ENOUGH_ARGUMENTS_IN_STACK, operationStr, numOfNeededArguments, argsStackSize));
            return ResponseEntity.status(statusCode).body(resultResp);
        }

        List<Integer> argumentsToCalc = new ArrayList<>();

        for (int i = 0; i < numOfNeededArguments; i++) {
            argumentsToCalc.add( argsStack.pop());
            argsStackSize--;
        }

        CalcResult calcResult = CalcUtill.performOp(operationStr, argumentsToCalc);

        if (!calcResult.isSucceeded()) {

            for (int i = argumentsToCalc.size() - 1; i >= 0 ; i--) {
                argsStack.push(argumentsToCalc.get(i));
                argsStackSize++;
            }


            statusCode = HttpStatus.CONFLICT;
            resultResp = new ResultResp(calcResult.getDetails());
        } else {
            resultResp = new ResultResp(calcResult.getValue());
        }

        return ResponseEntity.status(statusCode).body(resultResp);
    }


    @DeleteMapping ("/stack/arguments")
    public ResponseEntity<ResultResp> stackRemoveArguments ( @RequestParam ("count") String countStr) {

        int count = Integer.parseInt(countStr);

        // checks if the removal can be applied
        if ( count > argsStackSize) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResultResp( String.format(CalcError.CANNOT_REMOVE_FROM_STACK, count, argsStackSize)));
        }

        // remove the arguments
        while (count > 0 ) {

            argsStack.pop();
            argsStackSize--;
            count--;
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResultResp(argsStackSize));
    }


}
