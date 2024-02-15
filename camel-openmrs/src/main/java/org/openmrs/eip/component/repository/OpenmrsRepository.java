package org.openmrs.eip.component.repository;

import java.util.List;

import org.openmrs.eip.component.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

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
	
	/**
	 * Gets the id of the row that comes after the row matching the specified id.
	 * 
	 * @param lastProcessedId the last processed id.
	 * @return next row id
	 */
	@Query("SELECT MIN(e.id) FROM #{#entityName} e WHERE e.id > :lastProcessedId")
	Long getNextId(@Param("lastProcessedId") Long lastProcessedId);
	
	/**
	 * Gets the maximum row id
	 * 
	 * @return maximum row id
	 */
	@Query("SELECT MAX(e.id) FROM #{#entityName} e")
	Long getMaxId();
	
}
