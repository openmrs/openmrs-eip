package org.openmrs.eip.app.management.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseRetryQueueItem extends AbstractEntity {
	
	@Column(name = "exception_type")
	private String exceptionType;
	
	@Column(name = "cause_exception_type")
	private String causeExceptionType;
	
	@Column(length = 1024)
	private String message;
	
	@Column(name = "cause_message", length = 1024)
	private String causeMessage;
	
	@Column(name = "attempt_count", nullable = false)
	private Integer attemptCount = 1;
	
	@Column(name = "date_Changed")
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
	 * Gets the causeExceptionType
	 *
	 * @return the causeExceptionType
	 */
	public String getCauseExceptionType() {
		return causeExceptionType;
	}
	
	/**
	 * Sets the causeExceptionType
	 *
	 * @param causeExceptionType the causeExceptionType to set
	 */
	public void setCauseExceptionType(String causeExceptionType) {
		this.causeExceptionType = causeExceptionType;
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
	 * Gets the causeMessage
	 *
	 * @return the causeMessage
	 */
	public String getCauseMessage() {
		return causeMessage;
	}
	
	/**
	 * Sets the causeMessage
	 *
	 * @param causeMessage the causeMessage to set
	 */
	public void setCauseMessage(String causeMessage) {
		this.causeMessage = causeMessage;
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
