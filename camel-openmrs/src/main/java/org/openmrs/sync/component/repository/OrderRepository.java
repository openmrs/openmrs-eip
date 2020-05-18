package org.openmrs.sync.component.repository;

import org.openmrs.sync.component.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends SyncEntityRepository<Order> {

    @Override
    default List<Order> findModelsChangedAfterDate(LocalDateTime lastSyncDate) {
        return null;
    }
}
