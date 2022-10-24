package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.BaseEntity;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.LocalDateTime;
import java.util.List;

@NoRepositoryBean
public interface SyncEntityRepository<E extends BaseEntity> extends OpenmrsRepository<E> {
	
	/**
	 * find all entities created or modified after the given date
	 * 
	 * @param lastSyncDate the last sync date
	 * @return list of entities
	 */
	List<E> findModelsChangedAfterDate(LocalDateTime lastSyncDate);
}
