package com.example.utillity.log;

public class LogMessage {

    public static final String INCOMING_REQUEST_LOG_INFO = "Incoming request | #%d | resource: %s | HTTP Verb %s";

    public static final String REQUEST_DURATION_LOG_DEBUG = "request #%d duration: %dms";

    public static final String STACK_SIZE_LOG_INFO = "Stack size is %d";

    public static final String STACK_CONTENT_LOG_DEBUG = "Stack content (first == top): [%s]";

    public static final String SUFFIX_LOG_ALL = " | request #%d";
}
