package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.common.Address;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
public class PersonAddressModel extends BaseChangeableDataModel {
	
	private String personUuid;
	
	private boolean preferred;
	
	private Address address;
	
	private LocalDateTime startDate;
	
	private LocalDateTime endDate;
	
	/**
	 * Gets the personUuid
	 *
	 * @return the personUuid
	 */
	public String getPersonUuid() {
		return personUuid;
	}
	
	/**
	 * Sets the personUuid
	 *
	 * @param personUuid the personUuid to set
	 */
	public void setPersonUuid(String personUuid) {
		this.personUuid = personUuid;
	}
	
	/**
	 * Gets the preferred
	 *
	 * @return the preferred
	 */
	public boolean isPreferred() {
		return preferred;
	}
	
	/**
	 * Sets the preferred
	 *
	 * @param preferred the preferred to set
	 */
	public void setPreferred(boolean preferred) {
		this.preferred = preferred;
	}
	
	/**
	 * Gets the address
	 *
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}
	
	/**
	 * Sets the address
	 *
	 * @param address the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
	}
	
	/**
	 * Gets the startDate
	 *
	 * @return the startDate
	 */
	public LocalDateTime getStartDate() {
		return startDate;
	}
	
	/**
	 * Sets the startDate
	 *
	 * @param startDate the startDate to set
	 */
	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * Gets the endDate
	 *
	 * @return the endDate
	 */
	public LocalDateTime getEndDate() {
		return endDate;
	}
	
	/**
	 * Sets the endDate
	 *
	 * @param endDate the endDate to set
	 */
	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}
}
