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
public class SimpleHttpServerExApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimpleHttpServerExApplication.class, args);
    }


}
