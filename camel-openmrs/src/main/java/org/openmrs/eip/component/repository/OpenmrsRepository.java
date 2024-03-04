package org.openmrs.eip.component.repository;

import java.util.List;

import org.openmrs.eip.component.entity.BaseEntity;
import org.springframework.data.domain.Pageable;
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
	 * Gets the count of rows matching any of the specified uuids
	 *
	 * @param uuids list of uuids to match
	 * @return the count of matches
	 */
	int countByUuidIn(List<String> uuids);
	
	/**
	 * Gets the id of the row that comes after the row matching the specified offset id.
	 * 
	 * @param id offset id
	 * @return next row id
	 */
	@Query("SELECT MIN(e.id) FROM #{#entityName} e WHERE e.id > :id")
	Long getNextId(@Param("id") Long id);
	
	/**
	 * Gets the maximum row id
	 * 
	 * @return maximum row id
	 */
	@Query("SELECT MAX(e.id) FROM #{#entityName} e")
	Long getMaxId();
	
	/**
	 * Gets the count of rows with the ids that are less than or equal to the specified id range.
	 *
	 * @param endId last id inclusive
	 * @return count of matches
	 */
	Long countByIdLessThanEqual(Long endId);
	
	/**
	 * Gets a batch of id and uuid pairs for rows with the ids that match the specified id range.
	 *
	 * @param offsetId offset id exclusive
	 * @param endId last id inclusive
	 * @param page Pageable object
	 * @return list of id and uuid pairs
	 */
	@Query("SELECT e.id, e.uuid FROM #{#entityName} e WHERE e.id > :offsetId AND e.id <= :endId ORDER BY e.id")
	List<Object[]> getIdAndUuidBatchToReconcile(@Param("offsetId") Long offsetId, @Param("endId") Long endId, Pageable page);
	
}
