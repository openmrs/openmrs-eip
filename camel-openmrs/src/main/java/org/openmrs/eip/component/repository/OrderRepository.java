package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends SyncEntityRepository<Order> {
	
	@Override
	default List<Order> findModelsChangedAfterDate(LocalDateTime lastSyncDate) {
		return null;
	}
}
