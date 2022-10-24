package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.common.Address;

@EqualsAndHashCode(callSuper = true)
public class LocationModel extends BaseChangeableMetadataModel {
	
	private Address address;
	
	private String parentLocationUuid;
	
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
	 * Gets the parentLocationUuid
	 *
	 * @return the parentLocationUuid
	 */
	public String getParentLocationUuid() {
		return parentLocationUuid;
	}
	
	/**
	 * Sets the parentLocationUuid
	 *
	 * @param parentLocationUuid the parentLocationUuid to set
	 */
	public void setParentLocationUuid(String parentLocationUuid) {
		this.parentLocationUuid = parentLocationUuid;
	}
}
