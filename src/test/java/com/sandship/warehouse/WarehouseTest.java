package com.sandship.warehouse;

import com.sandship.warehouse.exception.NoEnoughMaterialException;
import com.sandship.warehouse.impl.MaterialType;
import com.sandship.warehouse.impl.WarehouseImpl;
import com.sandship.warehouse.api.WarehouseMaterial;
import com.sandship.warehouse.api.WarehouseObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class WarehouseTest {

    private WarehouseImpl warehouse1;
    private WarehouseImpl warehouse2;
    private MaterialType ironType;
    private MaterialType copperType;

    @BeforeEach
    void setUp() {
        warehouse1 = new WarehouseImpl();
        warehouse2 = new WarehouseImpl();
        ironType = new MaterialType("Iron", "A metal material", "iron_icon", 1000);
        copperType = new MaterialType("Copper", "Another metal material", "copper_icon", 1000);
    }

    @Test
    void addMaterial() {
        warehouse1.addMaterial(ironType, 500);
        assertEquals(500, warehouse1.getMaterialQuantity(ironType));
    }

    @Test
    void removeMaterial() {
        warehouse1.addMaterial(copperType, 800);
        warehouse1.removeMaterial(copperType, 300);
        assertEquals(500, warehouse1.getMaterialQuantity(copperType));
    }

    @Test
    void observerMaterialAdded() {
        TestObserver observer = new TestObserver();
        warehouse1.registerObserver(observer);
        warehouse1.addMaterial(ironType, 500);
        assertTrue(observer.materialAddedCalled);
    }

    @Test
    void observerMaterialRemoved() {
        TestObserver observer = new TestObserver();
        warehouse1.registerObserver(observer);
        warehouse1.addMaterial(ironType, 800);
        warehouse1.removeMaterial(ironType, 300);
        assertTrue(observer.materialRemovedCalled);
    }

    @Test
    void putMaterialTo() {
        warehouse1.addMaterial(ironType, 1000);
        long transferredQuantity = warehouse1.putMaterialTo(warehouse2, ironType);
        assertEquals(1000, transferredQuantity);
        assertEquals(1000, warehouse2.getMaterialQuantity(ironType));
    }

    @Test
    void takeMaterialFrom() {
        warehouse1.addMaterial(ironType, 1000);
        long transferredQuantity = warehouse1.takeMaterialFrom(warehouse2, ironType);
        assertEquals(0, transferredQuantity);
        assertEquals(1000, warehouse1.getMaterialQuantity(ironType));
    }

    @Test
    void canPut() {
        warehouse1.addMaterial(ironType, 800);
        assertTrue(warehouse1.canPut(ironType, 200));
        assertFalse(warehouse1.canPut(ironType, 1000));
    }

    @Test
    void canTake() {
        warehouse1.addMaterial(ironType, 800);
        assertTrue(warehouse1.canTake(ironType, 500));
        assertFalse(warehouse1.canTake(ironType, 1000));
    }

    @Test
    void emptyAllMaterialTypes() {
        warehouse1.addMaterial(ironType, 500);
        warehouse1.addMaterial(copperType, 700);
        warehouse1.emptyAllMaterialTypes();
        assertEquals(0, warehouse1.getMaterialQuantity(ironType));
        assertEquals(0, warehouse1.getMaterialQuantity(copperType));
    }

    @Test
    void emptyWarehouse() {
        warehouse1.addMaterial(ironType, 500);
        warehouse1.addMaterial(copperType, 700);
        warehouse1.emptyWarehouse();
        assertEquals(0, warehouse1.getAllWarehouseMaterials().size());
    }

    @Test
    void emptyMaterialType() {
        warehouse1.addMaterial(ironType, 500);
        warehouse1.addMaterial(copperType, 700);
        warehouse1.emptyMaterialType(ironType);
        assertEquals(0, warehouse1.getMaterialQuantity(ironType));
    }

    @Test
    void increaseMaterialCapacity() {
        warehouse1.addMaterial(ironType, 500);
        long newCapacity = warehouse1.increaseMaterialCapacity(ironType, 1500);
        assertEquals(1500, newCapacity);
    }

    @Test
    void printState() {
        warehouse1.addMaterial(ironType, 500);
        warehouse1.addMaterial(copperType, 700);
        warehouse1.printState();
    }

    @Test
    void negativeAddMaterialQuantity() {
        assertThrows(IllegalArgumentException.class, () -> warehouse1.addMaterial(ironType, -500));
    }

    @Test
    void negativeRemoveMaterialQuantity() {
        assertThrows(IllegalArgumentException.class, () -> warehouse1.removeMaterial(ironType, -500));
    }

    @Test
    void removeNonExistingMaterial() {
        assertThrows(NoEnoughMaterialException.class, () -> warehouse1.removeMaterial(ironType, 500));
    }

    @Test
    void cannotPutNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> warehouse1.canPut(ironType, -200));
    }

    @Test
    void cannotTakeNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> warehouse1.canTake(ironType, -200));
    }

    @Test
    void transferMoreThanAvailableMaterial() {
        warehouse1.addMaterial(ironType, 500);
        assertThrows(NoEnoughMaterialException.class, () -> warehouse1.putMaterialTo(warehouse2, ironType, 1000));
    }

    @Test
    void transferNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> warehouse1.putMaterialTo(warehouse2, ironType, -500));
    }

    @Test
    void testIncreaseMaterialCapacity() {
        warehouse1.addMaterial(ironType, 500);
        long newCapacity = warehouse1.increaseMaterialCapacity(ironType, 1500);
        assertEquals(1500, newCapacity);
    }

    @Test
    void testGetAllWarehouseMaterials() {
        warehouse1.addMaterial(ironType, 500);
        warehouse1.addMaterial(copperType, 700);
        Set<WarehouseMaterial> allMaterials = warehouse1.getAllWarehouseMaterials();
        assertEquals(2, allMaterials.size());
    }

    @Test
    void testGetMaterialAvailableSpace() {
        // Initial material quantity
        long initialQuantity = 500;

        // Initial and expected available space
        long expectedAvailableSpace = ironType.initialCapacity() - initialQuantity;

        // Add some material to the warehouse
        warehouse1.addMaterial(ironType, initialQuantity);

        // Check the available space after adding material
        assertEquals(expectedAvailableSpace, warehouse1.getMaterialAvailableSpace(ironType));

        // Remove some material
        long removedQuantity = 200;
        warehouse1.removeMaterial(ironType, removedQuantity);

        // Check the available space after removing material
        expectedAvailableSpace += removedQuantity;
        assertEquals(expectedAvailableSpace, warehouse1.getMaterialAvailableSpace(ironType));
    }

    @Test
    void testGetMaterial() {
        warehouse1.addMaterial(ironType, 500);
        WarehouseMaterial material = warehouse1.getMaterial(ironType);
        assertEquals(500, material.getQuantity());
    }

    @Test
    void testEmptyWarehouse() {
        warehouse1.addMaterial(ironType, 500);
        warehouse1.addMaterial(copperType, 700);
        warehouse1.emptyWarehouse();
        assertTrue(warehouse1.getAllWarehouseMaterials().isEmpty());
    }

    private static class TestObserver implements WarehouseObserver {
        boolean materialAddedCalled = false;
        boolean materialRemovedCalled = false;

        @Override
        public void onMaterialAdded(MaterialType materialType, long quantity) {
            materialAddedCalled = true;
        }

        @Override
        public void onMaterialRemoved(MaterialType materialType, long quantity) {
            materialRemovedCalled = true;
        }
    }
}
