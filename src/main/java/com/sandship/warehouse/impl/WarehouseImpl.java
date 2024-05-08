package com.sandship.warehouse.impl;

import com.sandship.warehouse.exception.NegativeTransferringMaterialCount;
import com.sandship.warehouse.exception.NoEnoughMaterialException;
import com.sandship.warehouse.api.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@AllArgsConstructor
public class WarehouseImpl extends AbstractWarehouseSubject implements Warehouse {

    // LAST_INSTANCE_ID is just for test, id should be generated automatically in db.
    private static long LAST_INSTANCE_ID = 0;

    @Getter
    private long id;
    private ConcurrentHashMap<MaterialType, WarehouseMaterial> materialList;

    public WarehouseImpl() {
        this(LAST_INSTANCE_ID, new ConcurrentHashMap<>());
        LAST_INSTANCE_ID++;
    }

    @Override
    public Set<WarehouseMaterial> getAllWarehouseMaterials() {
        return materialList.values()
                .stream()
                .map(WarehouseMaterial::clone)
                .collect(Collectors.toSet());
    }

    @Override
    public long getMaterialQuantity(MaterialType materialType) {
        WarehouseMaterial material = this.materialList.get(materialType);
        return material != null ? material.getQuantity() : 0;
    }

    @Override
    public long getMaterialAvailableSpace(MaterialType materialType) {
        WarehouseMaterial material = this.materialList.get(materialType);
        return material == null ? materialType.initialCapacity() : material.getAvailableSpace();
    }

    @Override
    public WarehouseMaterial getMaterial(MaterialType materialType) {
        WarehouseMaterial material = this.materialList.get(materialType);
        return Objects.nonNull(material) ? material.clone() : null;
    }

    @Override
    public long addMaterial(MaterialType materialType, long quantity) {
        WarehouseMaterial material = this.materialList.get(materialType);
        if (material == null) {
            material = new WarehouseMaterialImpl(materialType);
            this.materialList.put(materialType, material);
        }
        synchronized (material) {
            material.put(quantity);
            this.notifyObserversMaterialAdded(materialType, quantity);
            return material.getQuantity();
        }
    }

    @Override
    public long removeMaterial(MaterialType materialType, long quantity) {
        WarehouseMaterial material = this.materialList.get(materialType);
        if (material == null) {
            throw new NoEnoughMaterialException(materialType);
        }
        synchronized (material) {
            material.take(quantity);
            this.notifyObserversMaterialRemoved(materialType, quantity);
            return material.getQuantity();
        }
    }

    @Override
    public long putMaterialTo(Warehouse destination, MaterialType materialType) {
        return transfareMaterial(this, destination, materialType, this.getMaterialQuantity(materialType));
    }

    public long putMaterialTo(Warehouse destination, MaterialType materialType, long quantity) {
        return transfareMaterial(this, destination, materialType, quantity);
    }

    @Override
    public long takeMaterialFrom(Warehouse source, MaterialType materialType) {
        return transfareMaterial(source, this, materialType, source.getMaterialQuantity(materialType));
    }


    @Override
    public long takeMaterialFrom(Warehouse source, MaterialType materialType, long quantity) {
        return transfareMaterial(source, this, materialType, quantity);
    }

    @Override
    public boolean canPut(MaterialType materialType, long quantity) {
        if (quantity < 0) throw new NegativeTransferringMaterialCount();
        WarehouseMaterial material = this.materialList.get(materialType);
        if (material != null) {
            return material.canPut(quantity);
        } else {
            return materialType.initialCapacity() >= quantity;
        }

    }

    @Override
    public boolean canTake(MaterialType materialType, long quantity) {
        if (quantity < 0) throw new NegativeTransferringMaterialCount();
        WarehouseMaterial material = this.materialList.get(materialType);
        if (material != null) {
            return material.canTake(quantity);
        } else {
            return false;
        }
    }

    @Override
    public void emptyAllMaterialTypes() {
        this.materialList.values().forEach(WarehouseMaterial::empty);
    }

    @Override
    public void emptyWarehouse() {
        this.materialList.clear();
    }

    @Override
    public void emptyMaterialType(MaterialType materialType) {
        this.materialList.get(materialType).empty();
    }

    @Override
    public long increaseMaterialCapacity(MaterialType materialType, long newCapacity) {
        return this.materialList.get(materialType).increaseCapacity(newCapacity);
    }

    @Override
    public void printState() {
        System.out.println("----------------------------");
        System.out.printf("Warehouse %d state: %n", id);
        materialList.forEach((key, value) -> System.out.println(key.name() + ": " + value.getQuantity()));
        System.out.println("----------------------------");
    }

    private static long transfareMaterial(Warehouse source, Warehouse destination, MaterialType materialType, long quantity) {
        if (quantity == 0) return 0;
        Object[] locks = getSynchronizationObjects(source, destination, materialType);
        synchronized (locks[0]) {
            synchronized (locks[1]) {
                if (!source.canTake(materialType, quantity)) throw new NoEnoughMaterialException(materialType);
                long destinationSpace = destination.getMaterialAvailableSpace(materialType);
                long transferringQuantity = Math.min(quantity, destinationSpace);
                source.removeMaterial(materialType, transferringQuantity);
                destination.addMaterial(materialType, transferringQuantity);
                return transferringQuantity;
            }
        }
    }

    // this method returns synchronization objects in same order regardless of the order of the warehouses.
    // by these we avoid deadlock.
    private static Object[] getSynchronizationObjects(Warehouse source, Warehouse destination, MaterialType materialType) {
        Object[] locks = new Object[2];
        WarehouseMaterial sourceWarehouseMaterial = getSourceWarehouseForLock((WarehouseImpl) source, materialType);
        WarehouseMaterial destinationWarehouseMaterial = getDestinationWarehouseForLock((WarehouseImpl) destination, materialType);
        if (source.getId() > destination.getId()) {
            locks[0] = sourceWarehouseMaterial;
            locks[1] = destinationWarehouseMaterial;
        } else {
            locks[1] = sourceWarehouseMaterial;
            locks[0] = destinationWarehouseMaterial;
        }
        return locks;
    }

    private static WarehouseMaterial getSourceWarehouseForLock(WarehouseImpl source, MaterialType materialType) {
        WarehouseMaterial sourceWarehouseMaterial = source.materialList.get(materialType);
        if (sourceWarehouseMaterial != null) {
            return sourceWarehouseMaterial;
        }
        throw new NoEnoughMaterialException(materialType);
    }

    private static WarehouseMaterial getDestinationWarehouseForLock(WarehouseImpl destination, MaterialType materialType) {
        if (destination.getMaterial(materialType) == null) {
            destination.addMaterial(materialType, 0);
        }
        return destination.materialList.get(materialType);

    }
}
