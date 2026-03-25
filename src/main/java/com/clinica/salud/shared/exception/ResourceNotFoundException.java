package com.clinica.salud.shared.exception;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String entity, String id) {
        super(String.format("%s not found with id: %s", entity, id));
    }
}
