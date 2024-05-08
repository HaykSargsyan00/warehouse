package com.sandship.warehouse.api;

import com.sandship.warehouse.impl.MaterialType;

public interface WarehouseObserver {
    void onMaterialAdded(MaterialType materialType, long quantity);

    void onMaterialRemoved(MaterialType materialType, long quantity);
}
