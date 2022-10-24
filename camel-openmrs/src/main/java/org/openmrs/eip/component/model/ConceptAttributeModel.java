package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ConceptAttributeModel extends AttributeModel {
	
	private String conceptClassUuid;
	
	private String conceptDatatypeUuid;
	
	/**
	 * Gets the conceptClassUuid
	 *
	 * @return the conceptClassUuid
	 */
	public String getConceptClassUuid() {
		return conceptClassUuid;
	}
	
	/**
	 * Sets the conceptClassUuid
	 *
	 * @param conceptClassUuid the conceptClassUuid to set
	 */
	public void setConceptClassUuid(String conceptClassUuid) {
		this.conceptClassUuid = conceptClassUuid;
	}
	
	/**
	 * Gets the conceptDatatypeUuid
	 *
	 * @return the conceptDatatypeUuid
	 */
	public String getConceptDatatypeUuid() {
		return conceptDatatypeUuid;
	}
	
	/**
	 * Sets the conceptDatatypeUuid
	 *
	 * @param conceptDatatypeUuid the conceptDatatypeUuid to set
	 */
	public void setConceptDatatypeUuid(String conceptDatatypeUuid) {
		this.conceptDatatypeUuid = conceptDatatypeUuid;
	}
}
