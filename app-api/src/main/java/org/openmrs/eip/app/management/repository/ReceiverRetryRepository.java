package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.component.SyncOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReceiverRetryRepository extends JpaRepository<ReceiverRetryQueueItem, Long> {
	
	/**
	 * Gets the lost of all retry ids
	 *
	 * @return list of ids
	 */
	@Query("SELECT r.id FROM ReceiverRetryQueueItem r ORDER BY r.dateReceived ASC")
	List<Long> getIds();
	
	/**
	 * Gets the list of retries matching the specified ids.
	 *
	 * @param ids list of ids to match
	 * @return list of retries
	 */
	List<ReceiverRetryQueueItem> getByIdInOrderByDateReceivedAsc(List<Long> ids);
	
	/**
	 * Gets the count of retry items matching the specified identifier and model class names
	 *
	 * @param identifier the identifier to match
	 * @param modelClassNames model class names to match
	 * @return count of matching retry items
	 */
	long countByIdentifierAndModelClassNameIn(String identifier, List<String> modelClassNames);
	
	/**
	 * Checks if any row exists matching the specified identifier, operations and model class names.
	 *
	 * @param identifier the identifier to match
	 * @param modelClasses the model class names to match
	 * @param operations the operations to match
	 * @return true if a match is found otherwise false
	 */
	boolean existsByIdentifierAndModelClassNameInAndOperationIn(String identifier, List<String> modelClasses,
	                                                            List<SyncOperation> operations);
	
}
