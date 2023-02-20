package com.soam.service;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, int id) {
        super(String.format("%s not found for id %d", entityName, id));
    }
}
