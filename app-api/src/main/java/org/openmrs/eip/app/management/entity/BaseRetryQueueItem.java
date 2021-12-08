package org.openmrs.eip.app.management.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseRetryQueueItem extends AbstractEntity {
	
	@Column(name = "exception_type", nullable = false)
	private String exceptionType;
	
	@Column(length = 1024)
	private String message;
	
	@Column(name = "attempt_count", nullable = false)
	private Integer attemptCount = 1;
	
	@Column(name = "date_changed")
	private Date dateChanged;
	
	/**
	 * Gets the exceptionType
	 *
	 * @return the exceptionType
	 */
	public String getExceptionType() {
		return exceptionType;
	}
	
	/**
	 * Sets the exceptionType
	 *
	 * @param exceptionType the exceptionType to set
	 */
	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}
	
	/**
	 * Gets the message
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets the message
	 *
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Gets the attemptCount
	 *
	 * @return the attemptCount
	 */
	public Integer getAttemptCount() {
		return attemptCount;
	}
	
	/**
	 * Sets the attemptCount
	 *
	 * @param attemptCount the attemptCount to set
	 */
	public void setAttemptCount(Integer attemptCount) {
		this.attemptCount = attemptCount;
	}
	
	/**
	 * Gets the dateChanged
	 *
	 * @return the dateChanged
	 */
	public Date getDateChanged() {
		return dateChanged;
	}
	
	/**
	 * Sets the dateChanged
	 *
	 * @param dateChanged the dateChanged to set
	 */
	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
	}
	
}
