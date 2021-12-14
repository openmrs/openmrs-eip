package org.openmrs.eip.app.management.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

/**
 * Base class for sync request classes for the Sender and Receiver
 */
@MappedSuperclass
public abstract class BaseSyncRequest extends AbstractEntity {
	
	@NotNull
	@Column(name = "table_name", nullable = false, updatable = false)
	private String tableName;
	
	@NotNull
	@Column(nullable = false, updatable = false)
	private String identifier;
	
	@NotNull
	@Column(name = "request_uuid", nullable = false, unique = true, updatable = false, length = 38)
	private String requestUuid;
	
	@NotNull
	@Column(nullable = false, length = 1)
	@Access(AccessType.FIELD)
	private boolean found;
	
	/**
	 * Gets the tableName
	 *
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	
	/**
	 * Sets the tableName
	 *
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * Gets the identifier
	 *
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * Sets the identifier
	 *
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * Gets the requestUuid
	 *
	 * @return the requestUuid
	 */
	public String getRequestUuid() {
		return requestUuid;
	}
	
	/**
	 * Sets the requestUuid
	 *
	 * @param requestUuid the requestUuid to set
	 */
	public void setRequestUuid(String requestUuid) {
		this.requestUuid = requestUuid;
	}
	
	/**
	 * Gets found
	 *
	 * @return found
	 */
	public boolean getFound() {
		return found;
	}
	
	/**
	 * Sets the found to true
	 */
	protected void markAsFound() {
		this.found = true;
	}
	
}
