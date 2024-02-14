package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.receiver.ReconciliationMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReconciliationMsgRepository extends JpaRepository<ReconciliationMessage, Long> {
	
	/**
	 * Gets a batch of reconciliation messages that are not fully processed.
	 * 
	 * @param page {@link Pageable} object
	 * @return list of reconciliation messages
	 */
	@Query("SELECT m FROM ReconciliationMessage m WHERE m.processedCount < m.batchSize")
	List<ReconciliationMessage> getIncompleteMessages(Pageable page);
	
}
