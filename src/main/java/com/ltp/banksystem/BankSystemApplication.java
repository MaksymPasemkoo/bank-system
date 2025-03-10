package com.ltp.banksystem;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankSystemApplication {

    public static void main(String[] args) {
        final Dotenv dotenv = Dotenv.load();
        System.setProperty("DB_USER",dotenv.get("DB_USER"));
        System.setProperty("DB_PASSWORD",dotenv.get("DB_PASSWORD"));
        SpringApplication.run(BankSystemApplication.class, args);
    }

}
