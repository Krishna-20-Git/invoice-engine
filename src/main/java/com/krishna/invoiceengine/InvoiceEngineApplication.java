package com.krishna.invoiceengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InvoiceEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(InvoiceEngineApplication.class, args);
    }
}