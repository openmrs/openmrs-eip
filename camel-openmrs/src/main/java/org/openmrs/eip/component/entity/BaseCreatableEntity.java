package org.openmrs.eip.component.entity;

import java.time.LocalDateTime;

import org.openmrs.eip.component.entity.light.UserLight;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

@MappedSuperclass
public abstract class BaseCreatableEntity extends BaseEntity {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "creator")
	private UserLight creator;
	
	@NotNull
	@Column(name = "date_created")
	private LocalDateTime dateCreated;
	
	/**
	 * Gets the creator
	 *
	 * @return the creator
	 */
	public UserLight getCreator() {
		return creator;
	}
	
	/**
	 * Sets the creator
	 *
	 * @param creator the creator to set
	 */
	public void setCreator(UserLight creator) {
		this.creator = creator;
	}
	
	/**
	 * Gets the dateCreated
	 *
	 * @return the dateCreated
	 */
	public LocalDateTime getDateCreated() {
		return dateCreated;
	}
	
	/**
	 * Sets the dateCreated
	 *
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}
	
}
