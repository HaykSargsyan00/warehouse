package com.sandship.warehouse.api;

import com.sandship.warehouse.impl.MaterialType;

public interface WarehouseMaterial {
    long getQuantity();

    long getCapacity();

    long getAvailableSpace();

    MaterialType getMaterialType();

    boolean canPut(long quantity);

    boolean canTake(long quantity);

    long put(long quantity);

    long take(long quantity);

    long increaseCapacity(long newCapacity);

    void empty();

    WarehouseMaterial clone();
}
