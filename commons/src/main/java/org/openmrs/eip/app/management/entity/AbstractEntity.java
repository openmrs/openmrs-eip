package org.openmrs.eip.app.management.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Column(name = "date_created", nullable = false, updatable = false)
	private Date dateCreated;
	
	/**
	 * Gets the id
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * Sets the id
	 *
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * Gets the dateCreated
	 *
	 * @return the dateCreated
	 */
	public Date getDateCreated() {
		return dateCreated;
	}
	
	/**
	 * Sets the dateCreated
	 *
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		
		if (other == null || !getClass().isAssignableFrom(other.getClass())) {
			return false;
		}
		
		AbstractEntity otherObj = (AbstractEntity) other;
		if (getId() == null && otherObj.getId() == null) {
			return super.equals(other);
		}
		
		return getId().equals(otherObj.getId());
	}
	
	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : super.hashCode();
	}
	
}
