package com.example.nvt.exceptions;

public class NotFoundException extends RuntimeException {
    private String message;

    public NotFoundException() {}

    public NotFoundException(String msg)
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
