package org.openmrs.eip.app.management.repository;

import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SenderSyncMessageRepository extends JpaRepository<SenderSyncMessage, Long> {
	
	/**
	 * Deletes all the sender sync messages matching the given messaged uuid
	 * 
	 * @param messageUuid the message uuid
	 * @return the count of the deleted sync messages
	 */
	@Transactional(transactionManager = MGT_TX_MGR)
	long deleteByMessageUuid(String messageUuid);
	
	/**
	 * Gets all sync messages with status NEW
	 *
	 * @param page the {@link Pageable} object
	 * @return the list sync messages
	 */
	@Query("SELECT m FROM SenderSyncMessage m WHERE m.status = 'NEW' ORDER BY m.dateCreated ASC, m.id ASC")
	List<SenderSyncMessage> getNewSyncMessages(Pageable page);
	
}
