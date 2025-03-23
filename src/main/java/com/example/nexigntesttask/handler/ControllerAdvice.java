package com.example.nexigntesttask.handler;

import com.example.nexigntesttask.dto.ErrorResponse;
import com.example.nexigntesttask.handler.exceptions.FileWriteException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(FileWriteException.class)
    public ErrorResponse handleFileWriteException(FileWriteException e) {
        return new ErrorResponse(e.getMessage());
    }


}
