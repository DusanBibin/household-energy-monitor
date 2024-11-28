package com.example.nvt.exceptions;

public class InvalidInputException extends RuntimeException {
    private String message;

    public InvalidInputException() {}

    public InvalidInputException(String msg)
    {
        super(msg);
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
