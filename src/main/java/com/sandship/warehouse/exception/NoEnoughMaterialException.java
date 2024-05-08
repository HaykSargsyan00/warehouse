package com.sandship.warehouse.exception;

import com.sandship.warehouse.impl.MaterialType;

public class NoEnoughMaterialException extends IllegalArgumentException {
    public NoEnoughMaterialException(MaterialType type) {
        super("Insufficient quantity of material: " + type.name());
    }
}
