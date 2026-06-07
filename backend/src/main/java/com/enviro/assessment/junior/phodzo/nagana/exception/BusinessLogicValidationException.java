package com.enviro.assessment.junior.phodzo.nagana.exception;

public class BusinessLogicValidationException extends RuntimeException{
    public BusinessLogicValidationException(String message){
        super(message);
    }
    //this class creates and carries the error message from runtimeexception
}
