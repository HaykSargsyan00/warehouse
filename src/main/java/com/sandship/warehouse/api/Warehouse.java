package com.sandship.warehouse.api;

import com.sandship.warehouse.impl.MaterialType;

import java.util.Set;

public interface Warehouse extends WarehouseSubject {
    long getId();

    Set<WarehouseMaterial> getAllWarehouseMaterials();

    long getMaterialQuantity(MaterialType materialType);

    long getMaterialAvailableSpace(MaterialType materialType);

    WarehouseMaterial getMaterial(MaterialType materialType);

    long addMaterial(MaterialType materialType, long quantity);

    long removeMaterial(MaterialType materialType, long quantity);

    long putMaterialTo(Warehouse destination, MaterialType materialType);

    long putMaterialTo(Warehouse destination, MaterialType materialType, long quantity);

    long takeMaterialFrom(Warehouse source, MaterialType materialType);

    long takeMaterialFrom(Warehouse source, MaterialType materialType, long quantity);

    boolean canPut(MaterialType materialType, long quantity);

    boolean canTake(MaterialType materialType, long quantity);

    void emptyAllMaterialTypes();

    void emptyWarehouse();

    void emptyMaterialType(MaterialType materialType);

    long increaseMaterialCapacity(MaterialType ironType, long newCapacity);

    void printState();

}
