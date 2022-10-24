package org.openmrs.eip.component.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonAttributeModel extends BaseChangeableDataModel {
	
	private String value;
	
	private String personUuid;
	
	private String personAttributeTypeUuid;
	
	/**
	 * Gets the value
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Sets the value
	 *
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
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
	 * Gets the personAttributeTypeUuid
	 *
	 * @return the personAttributeTypeUuid
	 */
	public String getPersonAttributeTypeUuid() {
		return personAttributeTypeUuid;
	}
	
	/**
	 * Sets the personAttributeTypeUuid
	 *
	 * @param personAttributeTypeUuid the personAttributeTypeUuid to set
	 */
	public void setPersonAttributeTypeUuid(String personAttributeTypeUuid) {
		this.personAttributeTypeUuid = personAttributeTypeUuid;
	}
}
