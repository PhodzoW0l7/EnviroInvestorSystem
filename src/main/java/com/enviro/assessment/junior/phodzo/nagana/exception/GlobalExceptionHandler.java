package com.enviro.assessment.junior.phodzo.nagana.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

//serialized error responses as Json
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessLogicValidationException.class)
    public ResponseEntity<Map<String,String>> handleBusinessRulesExceptions(BusinessLogicValidationException exception){
        Map<String, String> error=new HashMap<>();
        //wraps the error message in a key value pair JSON object
        error.put("error",exception.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleInputValidation(MethodArgumentNotValidException exception){
        Map<String, String> errors=new HashMap<>();
        //inspects all the validation failures
        exception.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));

        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);

    }
}
