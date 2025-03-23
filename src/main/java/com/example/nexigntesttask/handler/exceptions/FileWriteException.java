package com.example.nexigntesttask.handler.exceptions;

public class FileWriteException extends RuntimeException {
    private static final String MESSAGE = "FileWriteException";

    public FileWriteException() {
        super(MESSAGE);
    }
}
