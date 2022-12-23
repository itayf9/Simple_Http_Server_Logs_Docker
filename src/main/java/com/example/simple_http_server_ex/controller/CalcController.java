package com.example.simple_http_server_ex.controller;

import com.example.request.ArgReq;
import com.example.request.CalcReq;
import com.example.utillity.*;
import com.example.response.ResultResp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;



@RestController
public class CalcController {

    public static Logger requestLogger = LogManager.getLogger("request-logger");

    private Stack<Integer> argsStack;
    private int argsStackSize;

    private int requestCount;

    public CalcController() {
        argsStack = new Stack<>();
        argsStackSize = 0;
        requestCount = 0;
    }

    @PostMapping(value = "/independent/calculate",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResultResp> independentCalculation(@RequestBody CalcReq calcReq) {

        long start = System.nanoTime();
        this.requestCount++;
        requestLogger.info(String.format(LogMessage.INCOMING_REQUEST_LOG_INFO, requestCount, "/independent/calculate", "POST")+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

        CalcResult calcResult = CalcUtill.performOp(calcReq.getOperation(), calcReq.getArguments());

        long end = System.nanoTime();
        requestLogger.debug(String.format(LogMessage.REQUEST_DURATION_LOG_DEBUG, requestCount, (end - start)/1000000)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

        if (!calcResult.isSucceeded()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResultResp(calcResult.getDetails()));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResultResp(calcResult.getValue()));
        }
    }

    @GetMapping(value = "/stack/size",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResultResp> getStackSize () {
        long start = System.nanoTime();
        this.requestCount++;
        requestLogger.info(String.format(LogMessage.INCOMING_REQUEST_LOG_INFO, requestCount, "/stack/size", "GET")+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
        ResponseEntity<ResultResp> responseResult = ResponseEntity.status(HttpStatus.OK)
                .body(new ResultResp(argsStackSize));
        long end = System.nanoTime();
        requestLogger.debug(String.format(LogMessage.REQUEST_DURATION_LOG_DEBUG, requestCount, (end - start)/1000000)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

        return responseResult;
    }

    @PutMapping(value = "/stack/arguments",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResultResp> addArguments(@RequestBody ArgReq argReq) {

        long start = System.nanoTime();
        requestLogger.info(String.format(LogMessage.INCOMING_REQUEST_LOG_INFO, requestCount, "/stack/arguments", "PUT")+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
        this.requestCount++;

        // adds the arguments
        for (Integer arg : argReq.getArguments()) {
            argsStack.push(arg);
            argsStackSize++;
        }

        ResponseEntity<ResultResp> responseResult = ResponseEntity.status(HttpStatus.OK)
                .body(new ResultResp(argsStackSize));

        long end = System.nanoTime();
        requestLogger.debug(String.format(LogMessage.REQUEST_DURATION_LOG_DEBUG, requestCount, (end - start)/1000000)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

        return responseResult;
    }

    @GetMapping(value = "/stack/operate",
    produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResultResp> performOperation(@RequestParam(value = "operation", defaultValue = "undefined") String operationStr) {

        long start = System.nanoTime();
        this.requestCount++;
        requestLogger.info(String.format(LogMessage.INCOMING_REQUEST_LOG_INFO, requestCount, "/stack/operate", "GET")+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

        Operation operation = Operation.getMatchOperation(operationStr);

        if (operation.equals(Operation.UNDEFINED)) {
            long end = System.nanoTime();
            requestLogger.debug(String.format(LogMessage.REQUEST_DURATION_LOG_DEBUG, requestCount, (end - start)/1000000)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResultResp(CalcError.UNKNOWN_OPERATION + operationStr));
        }

        // the operation is valid
        int numOfNeededArguments = operation.numOfNeededArgs();

        if (argsStackSize < numOfNeededArguments) {
            long end = System.nanoTime();
            requestLogger.debug(String.format(LogMessage.REQUEST_DURATION_LOG_DEBUG, requestCount, (end - start)/1000000)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResultResp(String.format(CalcError.NOT_ENOUGH_ARGUMENTS_IN_STACK, operationStr, numOfNeededArguments, argsStackSize)));
        }

        List<Integer> argumentsToCalc = new ArrayList<>();

        // extracts arguments to calculate
        for (int i = 0; i < numOfNeededArguments; i++) {
            argumentsToCalc.add( argsStack.pop());
            argsStackSize--;
        }

        // performs operation
        CalcResult calcResult = CalcUtill.performOp(operationStr, argumentsToCalc);

        long end = System.nanoTime();
        requestLogger.debug(String.format(LogMessage.REQUEST_DURATION_LOG_DEBUG, requestCount, (end - start)/1000000)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

        if (!calcResult.isSucceeded()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResultResp(calcResult.getDetails()));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResultResp(calcResult.getValue()));
        }



    }


    @DeleteMapping ("/stack/arguments")
    public ResponseEntity<ResultResp> stackRemoveArguments ( @RequestParam ("count") String countStr) {

        this.requestCount++;

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
