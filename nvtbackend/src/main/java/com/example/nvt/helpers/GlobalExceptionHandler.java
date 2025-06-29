package com.example.nvt.helpers;

import com.example.nvt.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }

    @ExceptionHandler(value
            = InvalidAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody ResponseMessage
    handleInvalidAuthenticationException(InvalidAuthenticationException ex)
    {
        return new ResponseMessage(ex.getMessage());
    }


    @ExceptionHandler(value
            = InvalidAuthorizationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ResponseMessage
    handleInvalidAuthorizationException(InvalidAuthorizationException ex)
    {
        return new ResponseMessage(ex.getMessage());
    }




//    @ExceptionHandler(value
//            = EmailNotConfirmedException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public @ResponseBody ResponseMessage
//    handleEmailNotConfirmedException(EmailNotConfirmedException ex)
//    {
//        return new ResponseMessage(ex.getMessage());
//    }


    @ExceptionHandler(value
            = InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ResponseMessage
    handleInvalidInputException(InvalidInputException ex)
    {
        return new ResponseMessage(ex.getMessage());
    }

    @ExceptionHandler(value
            = NotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ResponseMessage
    handleNotFoundExceptionException(NotFoundException ex)
    {
        return new ResponseMessage(ex.getMessage());
    }

}