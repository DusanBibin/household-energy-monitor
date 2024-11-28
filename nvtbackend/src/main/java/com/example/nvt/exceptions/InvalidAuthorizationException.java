package com.example.nvt.exceptions;

public class InvalidAuthorizationException extends RuntimeException {
    private String message;

    public InvalidAuthorizationException() {}

    public InvalidAuthorizationException(String msg)
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
