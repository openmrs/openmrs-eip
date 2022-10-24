package org.openmrs.eip.component.entity;

import org.openmrs.eip.component.entity.light.UserLight;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
