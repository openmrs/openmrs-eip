package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.OrderFrequency;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderFrequencyRepository extends SyncEntityRepository<OrderFrequency> {

    @Override
    default List<OrderFrequency> findModelsChangedAfterDate(LocalDateTime lastSyncDate) {
        return null;
    }

}