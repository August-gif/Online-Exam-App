package com.BrainTech.Online_exam_App_server.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// @ResponseStatus indique que cette exception doit retourner un code HTTP 500 (Internal Server Error)
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class StorageException extends RuntimeException{
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
