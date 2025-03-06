package com.example.task.myExceptions;

public class WrongParametersException extends RuntimeException {
    public WrongParametersException(String message) {
        super(message);
    }
}
