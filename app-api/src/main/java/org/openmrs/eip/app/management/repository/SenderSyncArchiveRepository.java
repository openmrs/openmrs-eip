package org.openmrs.eip.app.management.repository;

import java.util.Date;
import java.util.List;

import org.openmrs.eip.app.management.entity.sender.SenderSyncArchive;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SenderSyncArchiveRepository extends JpaRepository<SenderSyncArchive, Long> {
	
	/**
	 * Gets archives created on or before the specified maximum date
	 * 
	 * @param maxDate the maximum date to match against
	 * @param page {@link Pageable} instance
	 * @return list of archives
	 */
	List<SenderSyncArchive> findByDateCreatedLessThanEqual(Date maxDate, Pageable page);
	
}
