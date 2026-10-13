package com.api.libreria.exceptions;

// Se lanza cuando se rompe una regla de negocio o un dato no es válido -> HTTP 400
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
