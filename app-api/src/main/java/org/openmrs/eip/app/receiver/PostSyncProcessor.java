package org.openmrs.eip.app.receiver;

import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.openmrs.eip.component.SyncOperation;

/**
 * Super interface for post sync item processors
 * 
 * @param <T> the entity type
 */
public interface PostSyncProcessor<T extends AbstractEntity> extends HttpRequestProcessor<T> {
	
	/**
	 * Gets the model classname for the entity
	 *
	 * @param item the associated sync entity
	 * @return model classname
	 */
	String getModelClassName(T item);
	
	/**
	 * Gets the unique identifier for the entity
	 *
	 * @param item the associated sync entity
	 * @return unique identifier
	 */
	String getIdentifier(T item);
	
	/**
	 * Gets the {@link SyncOperation} for the sync event
	 *
	 * @param item the associated sync entity
	 * @return operation
	 */
	SyncOperation getOperation(T item);
	
}
