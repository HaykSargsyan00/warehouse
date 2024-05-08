package com.sandship.warehouse.impl;

import com.sandship.warehouse.api.WarehouseObserver;
import com.sandship.warehouse.api.WarehouseSubject;
import java.util.HashSet;

public abstract class AbstractWarehouseSubject implements WarehouseSubject {

    private final HashSet<WarehouseObserver> observers;

    AbstractWarehouseSubject() {
        observers = new HashSet<>();
    }

    @Override
    public void registerObserver(WarehouseObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void unregisterObserver(WarehouseObserver observer) {
        this.observers.remove(observer);
    }

    // Notify observers new quantity when material is added
    protected void notifyObserversMaterialAdded(MaterialType type, long quantity) {
        observers.forEach(observer -> observer.onMaterialAdded(type, quantity));
    }

    // Notify observers new quantity when material is removed
    protected void notifyObserversMaterialRemoved(MaterialType type, long quantity) {
        observers.forEach(observer -> observer.onMaterialRemoved(type, quantity));
    }
}
