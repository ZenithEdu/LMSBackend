package com.MiniLms.LMSBackend.exceptions;

public class SubjectAlreadyExistsException extends RuntimeException{
    public SubjectAlreadyExistsException(String message) {
        super(message);
    }
}
