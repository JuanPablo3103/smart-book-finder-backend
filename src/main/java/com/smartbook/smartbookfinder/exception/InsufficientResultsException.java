package com.smartbook.smartbookfinder.exception;

public class InsufficientResultsException extends RuntimeException {
    public InsufficientResultsException(String message) {
        super(message);
    }
}