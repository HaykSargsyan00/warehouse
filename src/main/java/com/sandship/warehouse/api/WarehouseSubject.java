package com.sandship.warehouse.api;

public interface WarehouseSubject {
    void registerObserver(WarehouseObserver observer);

    void unregisterObserver(WarehouseObserver observer);
}
