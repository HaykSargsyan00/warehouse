package com.sandship.warehouse;

import com.sandship.warehouse.exception.NoEnoughMaterialException;
import com.sandship.warehouse.exception.NoEnoughSpaceException;
import com.sandship.warehouse.impl.MaterialType;
import com.sandship.warehouse.impl.WarehouseMaterialImpl;
import com.sandship.warehouse.api.WarehouseMaterial;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WarehouseMaterialTest {
    MaterialType metal = new MaterialType("Metal", "description for metal", "icon name", 1000);

    @Test
    void getQuantityTest() {
        WarehouseMaterial warehouseMaterial = new WarehouseMaterialImpl(metal, 100);
        assertEquals(warehouseMaterial.getQuantity(), 0);

        warehouseMaterial.put(50);
        assertEquals(warehouseMaterial.getQuantity(), 50);
    }

    @Test
    void getCapacityTest() {
        WarehouseMaterial warehouseMaterial = new WarehouseMaterialImpl(metal, 100);
        assertEquals(warehouseMaterial.getCapacity(), 100);
    }

    @Test
    void getMaterialTypeTest() {
        WarehouseMaterial warehouseMaterial = new WarehouseMaterialImpl(metal, 100);
        assertEquals(warehouseMaterial.getMaterialType(), metal);
    }

    @Test
    void getAvailableSpaceTest() {
        WarehouseMaterial warehouseMaterial = new WarehouseMaterialImpl(metal, 100, 75);
        assertEquals(warehouseMaterial.getAvailableSpace(), 25);
    }

    @Test
    void canPutPositiveTest() {
        WarehouseMaterial warehouseMaterial = new WarehouseMaterialImpl(metal, 100);
        assertTrue(warehouseMaterial.canPut(10));
    }


    @Test
    void canPutNegativeTest() {
        WarehouseMaterial warehouseMaterial = new WarehouseMaterialImpl(metal, 100, 50);
        assertFalse(warehouseMaterial.canPut(100));
    }

    @Test
    void canTakePositiveTest() {
        WarehouseMaterial warehouseMaterial = new WarehouseMaterialImpl(metal, 100, 50);
        assertTrue(warehouseMaterial.canTake(10));
    }


    @Test
    void canTakeNegativeTest() {
        WarehouseMaterial warehouseMaterial = new WarehouseMaterialImpl(metal, 100, 50);
        assertFalse(warehouseMaterial.canTake(100));
    }

    @Test
    void warehouseMaterialTakeTest() {
        WarehouseMaterial warehouseMaterial = new WarehouseMaterialImpl(metal, 100, 50);
        warehouseMaterial.take(20);
        assertEquals(warehouseMaterial.getQuantity(), 30);
    }

    @Test
    void warehouseMaterialTakeInvalidQuantityTest() {
        WarehouseMaterial warehouseMaterial = new WarehouseMaterialImpl(metal, 100, 50);
        Exception exception = assertThrows(NoEnoughMaterialException.class, () -> warehouseMaterial.take(60));

        assertEquals(exception.getMessage(), "Insufficient quantity of material: " + metal.name());
    }

    @Test
    void warehouseMaterialPutTest() {
        WarehouseMaterial warehouseMaterial = new WarehouseMaterialImpl(metal, 100, 50);
        warehouseMaterial.put(20);
        assertEquals(warehouseMaterial.getQuantity(), 70);
    }

    @Test
    void warehouseMaterialPutInvalidQuantityTest() {
        WarehouseMaterial warehouseMaterial = new WarehouseMaterialImpl(metal, 100, 50);
        Exception exception = assertThrows(NoEnoughSpaceException.class, () -> warehouseMaterial.put(60));

        assertEquals(exception.getMessage(), "Not enough space for material: " + metal.name());
    }

    @Test
    void increaseCapacityTest() {
        WarehouseMaterial warehouseMaterial = new WarehouseMaterialImpl(metal, 100, 50);
        warehouseMaterial.increaseCapacity(150);
        assertEquals(warehouseMaterial.getCapacity(), 150);
    }

    @Test
    void decreaseCapacityTest() {
        WarehouseMaterial warehouseMaterial = new WarehouseMaterialImpl(metal, 100, 50);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> warehouseMaterial.increaseCapacity(50));
        assertEquals(exception.getMessage(), "New capacity must be greater than existing.");
    }

    @Test
    void emptyWarehouseMaterialTest() {
        WarehouseMaterial warehouseMaterial = new WarehouseMaterialImpl(metal, 100, 50);
        warehouseMaterial.empty();
        assertEquals(warehouseMaterial.getQuantity(), 0);
    }

}
