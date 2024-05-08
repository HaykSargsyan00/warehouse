package com.sandship.warehouse.exception;

import com.sandship.warehouse.impl.MaterialType;

public class NoEnoughSpaceException extends IllegalArgumentException {
    public NoEnoughSpaceException(MaterialType type) {
        super("Not enough space for material: " + type.name());
    }
}
