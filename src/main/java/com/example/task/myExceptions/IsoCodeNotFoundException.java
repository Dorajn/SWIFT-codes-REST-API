package com.example.task.myExceptions;

public class IsoCodeNotFoundException extends RuntimeException {
    public IsoCodeNotFoundException(String message) {
        super(message);
    }
}
