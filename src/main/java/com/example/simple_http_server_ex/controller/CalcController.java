package com.example.simple_http_server_ex.controller;

import com.example.request.ArgReq;
import com.example.request.CalcReq;
import com.example.utillity.*;
import com.example.response.ResultResp;
import com.example.utillity.log.LogMessage;
import com.example.utillity.log.LoggerName;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;



@RestController
public class CalcController {


    public static Logger requestLogger = LogManager.getLogger(LoggerName.LOGGER_NAME_REQUEST);
    public static Logger stackLogger = LogManager.getLogger(LoggerName.LOGGER_NAME_STACK);
    public static Logger independentLogger = LogManager.getLogger(LoggerName.LOGGER_NAME_INDEPENDENT);

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

        ResponseEntity<ResultResp> responseResult;
        if (!calcResult.isSucceeded()) {
            responseResult = ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResultResp(calcResult.getDetails()));
            independentLogger.error(String.format(LogMessage.SERVER_ENCOUNTERED_ERR_LOG_ERROR, calcResult.getDetails())+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
        } else {
            responseResult = ResponseEntity.status(HttpStatus.OK)
                    .body(new ResultResp(calcResult.getValue()));
            independentLogger.info(String.format(LogMessage.INDEPENDENT_PERFORM_OPERATION_LOG_INFO, calcReq.getOperation(), calcResult.getValue())+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
            independentLogger.debug(String.format(LogMessage.INDEPENDENT_PERFORM_OPERATION_LOG_DEBUG, calcReq.getOperation(), getArgsContentStr(calcReq.getArguments()), calcResult.getValue())+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
        }
        long end = System.nanoTime();
        requestLogger.debug(String.format(LogMessage.REQUEST_DURATION_LOG_DEBUG, requestCount, (end - start)/1000000)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

        return responseResult;
    }

    @GetMapping(value = "/stack/size",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResultResp> getStackSize () {
        long start = System.nanoTime();
        this.requestCount++;
        requestLogger.info(String.format(LogMessage.INCOMING_REQUEST_LOG_INFO, requestCount, "/stack/size", "GET")+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
        ResponseEntity<ResultResp> responseResult = ResponseEntity.status(HttpStatus.OK)
                .body(new ResultResp(argsStackSize));
        stackLogger.info(String.format(LogMessage.STACK_SIZE_LOG_INFO, argsStackSize)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
        stackLogger.debug(String.format(LogMessage.STACK_CONTENT_LOG_DEBUG, getStackContentStr())+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
        long end = System.nanoTime();
        requestLogger.debug(String.format(LogMessage.REQUEST_DURATION_LOG_DEBUG, requestCount, (end - start)/1000000)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
        return responseResult;
    }

    @PutMapping(value = "/stack/arguments",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResultResp> addArguments(@RequestBody ArgReq argReq) {

        long start = System.nanoTime();
        this.requestCount++;
        requestLogger.info(String.format(LogMessage.INCOMING_REQUEST_LOG_INFO, requestCount, "/stack/arguments", "PUT")+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));


        // adds the arguments
        for (Integer arg : argReq.getArguments()) {
            argsStack.push(arg);
            argsStackSize++;
        }
        stackLogger.info(String.format(LogMessage.ADDING_ARGUMENTS_LOG_INFO, argReq.getArguments().size(), argsStackSize)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
        stackLogger.debug(String.format(LogMessage.ADDING_ARGUMENTS_LOG_DEBUG, getArgsContentStr(argReq.getArguments()), argsStackSize- argReq.getArguments().size(), argsStackSize)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

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

        ResponseEntity<ResultResp> responseResult;

        if (operation.equals(Operation.UNDEFINED)) {

            responseResult = ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResultResp(CalcError.UNKNOWN_OPERATION + operationStr));
            long end = System.nanoTime();
            requestLogger.debug(String.format(LogMessage.REQUEST_DURATION_LOG_DEBUG, requestCount, (end - start)/1000000)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
            stackLogger.error(String.format(LogMessage.SERVER_ENCOUNTERED_ERR_LOG_ERROR, CalcError.UNKNOWN_OPERATION + operationStr)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

            return responseResult;
        }

        // the operation is valid
        int numOfNeededArguments = operation.numOfNeededArgs();

        if (argsStackSize < numOfNeededArguments) {

            responseResult = ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResultResp(String.format(CalcError.NOT_ENOUGH_ARGUMENTS_IN_STACK, operationStr, numOfNeededArguments, argsStackSize)));
            long end = System.nanoTime();
            requestLogger.debug(String.format(LogMessage.REQUEST_DURATION_LOG_DEBUG, requestCount, (end - start)/1000000)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
            stackLogger.error(String.format(LogMessage.SERVER_ENCOUNTERED_ERR_LOG_ERROR, String.format(CalcError.NOT_ENOUGH_ARGUMENTS_IN_STACK, operationStr, numOfNeededArguments, argsStackSize))+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

            return responseResult;
        }

        List<Integer> argumentsToCalc = new ArrayList<>();

        // extracts arguments to calculate
        for (int i = 0; i < numOfNeededArguments; i++) {
            argumentsToCalc.add( argsStack.pop());
            argsStackSize--;
        }

        // performs operation
        CalcResult calcResult = CalcUtill.performOp(operationStr, argumentsToCalc);

        if (!calcResult.isSucceeded()) {
            responseResult = ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResultResp(calcResult.getDetails()));
            stackLogger.error(String.format(LogMessage.SERVER_ENCOUNTERED_ERR_LOG_ERROR, calcResult.getDetails())+ String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
        } else {
            responseResult = ResponseEntity.status(HttpStatus.OK)
                    .body(new ResultResp(calcResult.getValue()));
            stackLogger.info(String.format(LogMessage.STACK_PERFORM_OPERATION_LOG_INFO, operationStr, calcResult.getValue(), argsStackSize)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
            stackLogger.debug(String.format(LogMessage.STACK_PERFORM_OPERATION_LOG_DEBUG, operationStr, getArgsContentStr(argumentsToCalc), calcResult.getValue())+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
        }

        long end = System.nanoTime();
        requestLogger.debug(String.format(LogMessage.REQUEST_DURATION_LOG_DEBUG, requestCount, (end - start)/1000000)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
        return responseResult;
    }


    @DeleteMapping ("/stack/arguments")
    public ResponseEntity<ResultResp> stackRemoveArguments ( @RequestParam ("count") String countStr) {

        long start = System.nanoTime();
        this.requestCount++;
        requestLogger.info(String.format(LogMessage.INCOMING_REQUEST_LOG_INFO, requestCount, "/stack/arguments", "DELETE")+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

        int count = Integer.parseInt(countStr);
        int originalCount = count;

        ResponseEntity<ResultResp> responseResult;

        // checks if the removal can be applied
        if ( count > argsStackSize) {

            responseResult = ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResultResp( String.format(CalcError.CANNOT_REMOVE_FROM_STACK, count, argsStackSize)));
            long end = System.nanoTime();
            requestLogger.debug(String.format(LogMessage.REQUEST_DURATION_LOG_DEBUG, requestCount, (end - start)/1000000)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));
            stackLogger.error(String.format(LogMessage.SERVER_ENCOUNTERED_ERR_LOG_ERROR, String.format(CalcError.CANNOT_REMOVE_FROM_STACK, count, argsStackSize))+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

            return responseResult;
        }

        // remove the arguments
        while (count > 0 ) {

            argsStack.pop();
            argsStackSize--;
            count--;
        }
        stackLogger.info(String.format(LogMessage.REMOVING_ARGUMENTS_LOG_INFO, originalCount, argsStackSize)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

        responseResult = ResponseEntity.status(HttpStatus.OK)
                .body(new ResultResp(argsStackSize));

        long end = System.nanoTime();
        requestLogger.debug(String.format(LogMessage.REQUEST_DURATION_LOG_DEBUG, requestCount, (end - start)/1000000)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

        return responseResult;
    }

    @GetMapping(value = "/logs/level",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> getCurrentLevelOfLogger ( @RequestParam(name = "logger-name") String loggerName ) {

        String level;

        long start = System.nanoTime();
        this.requestCount++;
        requestLogger.info(String.format(LogMessage.INCOMING_REQUEST_LOG_INFO, requestCount, "/logs/level", "GET")+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

        ResponseEntity<String> responseResult;

        // finds what logger is required
        if (loggerName.equals(LoggerName.LOGGER_NAME_REQUEST)) {
            level = requestLogger.getLevel().name();
            responseResult = ResponseEntity.status(HttpStatus.OK).body(level);
        } else if (loggerName.equals(LoggerName.LOGGER_NAME_STACK)) {
            level = stackLogger.getLevel().name();
            responseResult = ResponseEntity.status(HttpStatus.OK).body(level);
        } else if (loggerName.equals(LoggerName.LOGGER_NAME_INDEPENDENT)) {
            level = independentLogger.getLevel().name();
            responseResult = ResponseEntity.status(HttpStatus.OK).body(level);
        } else {
            level = "Error. This logger does not exist.";
            responseResult = ResponseEntity.status(HttpStatus.CONFLICT).body(level);
        }

        long end = System.nanoTime();
        requestLogger.debug(String.format(LogMessage.REQUEST_DURATION_LOG_DEBUG, requestCount, (end - start)/1000000)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

        return responseResult;

    }

    @PutMapping(value = "/logs/level",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> setCurrentLevelOfLogger (@RequestParam(name = "logger-name") String loggerName, @RequestParam(name = "logger-level") String loggerLevel) {

        String level;

        long start = System.nanoTime();
        this.requestCount++;
        requestLogger.info(String.format(LogMessage.INCOMING_REQUEST_LOG_INFO, requestCount, "/logs/level", "PUT")+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

        Level levelFromString = Level.getLevel(loggerLevel);

        ResponseEntity<String> responseResult;

        if (levelFromString == null) { // the level name is invalid
            level = "Error.this level does not exist";
            responseResult = ResponseEntity.status(HttpStatus.CONFLICT).body(level);
        } else {
            level = levelFromString.name();

            LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
            Configuration config = loggerContext.getConfiguration();

            LoggerConfig loggerConfig;

            if (loggerName.equals(LoggerName.LOGGER_NAME_REQUEST)) {
                loggerConfig = config.getLoggerConfig(requestLogger.getName());
            } else if (loggerName.equals(LoggerName.LOGGER_NAME_STACK)) {
                loggerConfig = config.getLoggerConfig(stackLogger.getName());
            } else if (loggerName.equals(LoggerName.LOGGER_NAME_INDEPENDENT)) {
                loggerConfig = config.getLoggerConfig(independentLogger.getName());
            } else {
                loggerConfig = null;
            }

            if (loggerConfig == null) { // the logger name is invalid
                level = "Error. This logger does not exist.";
                responseResult = ResponseEntity.status(HttpStatus.CONFLICT).body(level);
            } else {
                loggerConfig.setLevel(levelFromString);
                loggerContext.updateLoggers();
                responseResult = ResponseEntity.status(HttpStatus.OK).body(level);
            }

        }

        long end = System.nanoTime();
        requestLogger.debug(String.format(LogMessage.REQUEST_DURATION_LOG_DEBUG, requestCount, (end - start)/1000000)+String.format(LogMessage.SUFFIX_LOG_ALL, requestCount));

        return responseResult;
    }

    /**
     * converts a stack to a String
     * @return String that represents the stack
     */
    private String getStackContentStr() {
        Stack<Integer> s = new Stack<>();
        StringBuilder contentStrBuilder = new StringBuilder();

        while (!argsStack.isEmpty()) {
            contentStrBuilder.append(argsStack.peek());
            s.push(argsStack.pop());
            if (!argsStack.isEmpty()) {
                contentStrBuilder.append(",");
            }
        }

        while (!s.isEmpty()) {
            argsStack.push(s.pop());
        }

        return contentStrBuilder.toString();
    }

    /**
     * converts a list to a String
     * @param arguments a List to convert
     * @return a String that represents the list
     */
    private String getArgsContentStr(List<Integer> arguments) {
        StringBuilder contentSrtBuilder = new StringBuilder();

        for (int i = 0; i < arguments.size(); i++) {
            contentSrtBuilder.append(arguments.get(i));
            if (i!=arguments.size()-1) {
                contentSrtBuilder.append(",");
            }
        }

        return contentSrtBuilder.toString();
    }

}
