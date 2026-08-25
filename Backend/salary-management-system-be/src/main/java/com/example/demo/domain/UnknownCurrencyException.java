package com.example.demo.domain;

public class UnknownCurrencyException extends RuntimeException {

    public UnknownCurrencyException(String currency) {
        super("Unsupported currency: " + currency);
    }
}
