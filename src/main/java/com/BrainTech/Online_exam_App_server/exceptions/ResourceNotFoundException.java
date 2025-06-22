package com.BrainTech.Online_exam_App_server.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Indique que cette exception devrait entraîner une réponse HTTP 404 NOT FOUND
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{
    // Constructeur simple avec un message
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Constructeur avec un message et une cause (l'exception originale)
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
