package com.sandship.warehouse;

import com.sandship.warehouse.impl.MaterialType;
import com.sandship.warehouse.impl.WarehouseImpl;
import com.sandship.warehouse.api.Warehouse;
import com.sandship.warehouse.api.WarehouseObserver;

public class WarehouseApplication {

    public static void main(String[] args) {
        // Create material types
        MaterialType ironType = new MaterialType("Iron", "A metal material", "iron_icon", 1000);
        MaterialType copperType = new MaterialType("Copper", "Another metal material", "copper_icon", 1000);

        // Create warehouses
        Warehouse warehouse0 = new WarehouseImpl();
        Warehouse warehouse1 = new WarehouseImpl();

        // Register observers
        WarehouseObserver observer0 = new MaterialChangeObserver("Observer 0");
        WarehouseObserver observer1 = new MaterialChangeObserver("Observer 1");
        warehouse0.registerObserver(observer0);
        warehouse1.registerObserver(observer1);

        // Add materials to warehouse0
        warehouse0.addMaterial(ironType, 500);
        warehouse0.addMaterial(copperType, 700);

        // Print state of warehouse0
        System.out.println("Warehouse 0 state:");
        warehouse0.printState();

        // Print state of warehouse1
        System.out.println("Warehouse 1 state:");
        warehouse1.printState();

        // Transfer materials from warehouse0 to warehouse1
        warehouse0.putMaterialTo(warehouse1, ironType, 200);
        warehouse0.putMaterialTo(warehouse1, copperType, 300);

        // Print state of warehouse0 after transfer
        System.out.println("Warehouse 0 state after transfer:");
        warehouse0.printState();

        // Print state of warehouse1 after transfer
        System.out.println("Warehouse 1 state after transfer:");
        warehouse1.printState();
    }

    static class MaterialChangeObserver implements WarehouseObserver {
        private final String observerName;

        public MaterialChangeObserver(String observerName) {
            this.observerName = observerName;
        }

        @Override
        public void onMaterialAdded(MaterialType materialType, long quantity) {
            System.out.println(observerName + " observed material added: " + materialType.name() + ", Quantity: " + quantity);
        }

        @Override
        public void onMaterialRemoved(MaterialType materialType, long quantity) {
            System.out.println(observerName + " observed material removed: " + materialType.name() + ", Quantity: " + quantity);
        }
    }
}
