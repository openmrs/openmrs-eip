package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.component.SyncOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncMessageRepository extends JpaRepository<SyncMessage, Long> {
	
	/**
	 * Gets the count of sync messages matching the specified identifier and model class names
	 * 
	 * @param identifier the identifier to match
	 * @param modelClassNames model class names to match
	 * @return count of matching sync messages
	 */
	long countByIdentifierAndModelClassNameIn(String identifier, List<String> modelClassNames);
	
	/**
	 * Gets a page of sync messages for the specified site
	 *
	 * @param site the site to match
	 * @param pageable {@link Pageable} instance
	 * @return list of sync messages
	 */
	List<SyncMessage> getSyncMessageBySiteOrderByDateCreated(SiteInfo site, Pageable pageable);
	
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
