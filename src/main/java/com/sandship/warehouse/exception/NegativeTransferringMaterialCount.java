package com.sandship.warehouse.exception;

public class NegativeTransferringMaterialCount extends IllegalArgumentException {
    public NegativeTransferringMaterialCount() {
        super("Transferring material quantity must be positive.");
    }
}
