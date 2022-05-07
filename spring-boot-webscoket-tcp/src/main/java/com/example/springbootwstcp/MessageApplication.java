package com.example.springbootwstcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class MessageApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(MessageApplication.class, args);
    }
}