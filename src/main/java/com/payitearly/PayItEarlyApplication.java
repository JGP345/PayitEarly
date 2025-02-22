package com.payitearly;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.payitearly")
public class PayItEarlyApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayItEarlyApplication.class, args);
    }
}
