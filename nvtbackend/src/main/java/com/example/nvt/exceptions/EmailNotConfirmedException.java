package com.example.nvt.exceptions;

public class EmailNotConfirmedException extends RuntimeException{
    public EmailNotConfirmedException(String message) {
        super(message);
    }
}
