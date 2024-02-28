package org.openmrs.eip.app.management.repository;

import java.util.Date;
import java.util.List;

import org.openmrs.eip.app.management.entity.sender.DeletedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedEntityRepository extends JpaRepository<DeletedEntity, Long> {
	
	/**
	 * Gets all entities deleted from the specified table on or after the specified date
	 * 
	 * @param tableName the table to match
	 * @param date the date to match
	 * @return list of deleted entities
	 */
	List<DeletedEntity> getByTableNameIgnoreCaseAndDateCreatedGreaterThanEqual(String tableName, Date date);
	
	/**
	 * Gets all entities deleted from the specified table.
	 *
	 * @param tableName the table to match
	 * @return list of deleted entities
	 */
	List<DeletedEntity> getByTableNameIgnoreCase(String tableName);
	
}
