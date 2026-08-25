package com.example.demo.employee;

public class InvalidEmployeeDataException extends RuntimeException {

    public InvalidEmployeeDataException(String message) {
        super(message);
    }
}
