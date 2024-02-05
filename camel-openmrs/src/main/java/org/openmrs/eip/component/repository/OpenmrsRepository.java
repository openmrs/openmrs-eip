package org.openmrs.eip.component.repository;

import java.util.List;

import org.openmrs.eip.component.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OpenmrsRepository<E extends BaseEntity> extends JpaRepository<E, Long> {
	
	/**
	 * find entity by uuid
	 * 
	 * @param uuid the uuid
	 * @return an entity
	 */
	E findByUuid(String uuid);
	
	/**
	 * Checks if a row exists matching the specified uuid
	 *
	 * @param uuid the uuid
	 * @return true of a match is found otherwise false
	 */
	boolean existsByUuid(String uuid);
	
	/**
	 * Gets the count of rows matching the specified uuids
	 *
	 * @param uuids list of uuids to match
	 * @return the count of matches
	 */
	int countByUuidIn(List<String> uuids);
	
}
