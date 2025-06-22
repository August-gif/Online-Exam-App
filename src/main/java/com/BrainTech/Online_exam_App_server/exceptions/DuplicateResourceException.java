package com.BrainTech.Online_exam_App_server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Indique que cette exception devrait entraîner une réponse HTTP 409 CONFLICT
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException{
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
