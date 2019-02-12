package com.example.userservice.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message){
        super(message);
    }

    public UserAlreadyExistsException(Throwable cause){
        super(cause);
    }

    public UserAlreadyExistsException(String message, Throwable cause){
        super(message, cause);
    }
}
