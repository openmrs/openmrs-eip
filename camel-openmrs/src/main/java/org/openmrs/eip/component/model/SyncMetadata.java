package org.openmrs.eip.component.model;

import java.time.LocalDateTime;

/**
 * Encapsulates descriptive data about a sync payload i.e. the unique ID of the site sending the.
 * payload and the date it was sent.
 */
public class SyncMetadata {
	
	private String sourceIdentifier;
	
	private String operation;
	
	private LocalDateTime dateSent;
	
	/**
	 * Gets the sourceIdentifier
	 *
	 * @return the sourceIdentifier
	 */
	public String getSourceIdentifier() {
		return sourceIdentifier;
	}
	
	/**
	 * Sets the sourceIdentifier
	 *
	 * @param sourceIdentifier the sourceIdentifier to set
	 */
	public void setSourceIdentifier(String sourceIdentifier) {
		this.sourceIdentifier = sourceIdentifier;
	}
	
	/**
	 * Gets the operation
	 *
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}
	
	/**
	 * Sets the operation
	 *
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	/**
	 * Gets the dateSent
	 *
	 * @return the dateSent
	 */
	public LocalDateTime getDateSent() {
		return dateSent;
	}
	
	/**
	 * Sets the dateSent
	 *
	 * @param dateSent the dateSent to set
	 */
	public void setDateSent(LocalDateTime dateSent) {
		this.dateSent = dateSent;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{sourceIdentifier=" + sourceIdentifier + ", operation=" + operation
		        + ", dateSent=" + dateSent + "}";
	}
	
}
