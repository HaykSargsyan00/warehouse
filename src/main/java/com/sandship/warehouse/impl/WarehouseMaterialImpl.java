package com.sandship.warehouse.impl;

import com.sandship.warehouse.exception.NegativeTransferringMaterialCount;
import com.sandship.warehouse.exception.NoEnoughMaterialException;
import com.sandship.warehouse.exception.NoEnoughSpaceException;
import com.sandship.warehouse.api.WarehouseMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WarehouseMaterialImpl implements WarehouseMaterial {
    private MaterialType materialType;
    private long capacity;
    private long quantity;

    public WarehouseMaterialImpl(MaterialType materialType) {
        this(materialType, materialType.initialCapacity(), 0);
    }

    public WarehouseMaterialImpl(MaterialType materialType, long capacity) {
        this(materialType, capacity, 0);
    }

    public long getAvailableSpace() {
        return capacity - quantity;
    }

    @Override
    public boolean canPut(long quantity) {
        if (quantity < 0) throw new NegativeTransferringMaterialCount();
        return this.capacity - this.quantity >= quantity;
    }

    @Override
    public boolean canTake(long quantity) {
        if (quantity < 0) throw new NegativeTransferringMaterialCount();
        return this.quantity >= quantity;
    }

    public long put(long quantity) {
        if (!canPut(quantity)) throw new NoEnoughSpaceException(materialType);
        this.quantity += quantity;
        return this.quantity;
    }

    @Override
    public long take(long quantity) throws NoEnoughMaterialException, NegativeTransferringMaterialCount {
        if (!canTake(quantity)) throw new NoEnoughMaterialException(materialType);
        this.quantity -= quantity;
        return this.quantity;
    }

    @Override
    public long increaseCapacity(long newCapacity) {
        if (newCapacity <= this.capacity)
            throw new IllegalArgumentException("New capacity must be greater than existing.");
        capacity = newCapacity;
        return capacity;
    }

    @Override
    public void empty() {
        this.quantity = 0;
    }

    @Override
    public WarehouseMaterial clone() {
        return new WarehouseMaterialImpl(materialType, capacity, quantity);
    }
}
