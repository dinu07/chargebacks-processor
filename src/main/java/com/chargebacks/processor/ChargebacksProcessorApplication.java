package com.chargebacks.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChargebacksProcessorApplication {

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(ChargebacksProcessorApplication.class, args)));
    }
}

