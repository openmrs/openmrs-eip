package org.openmrs.eip.component.entity;

import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.OrderFrequencyLight;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "test_order")
@PrimaryKeyJoinColumn(name = "order_id")
public class TestOrder extends Order {
	
	@ManyToOne
	@JoinColumn(name = "specimen_source")
	private ConceptLight specimenSource;
	
	@Column(name = "laterality")
	private String laterality;
	
	@Column(name = "clinical_history")
	private String clinicalHistory;
	
	@ManyToOne
	@JoinColumn(name = "frequency")
	private OrderFrequencyLight frequency;
	
	@Column(name = "number_of_repeats")
	private Integer numberOfRepeats;
	
	/**
	 * Gets the specimenSource
	 *
	 * @return the specimenSource
	 */
	public ConceptLight getSpecimenSource() {
		return specimenSource;
	}
	
	/**
	 * Sets the specimenSource
	 *
	 * @param specimenSource the specimenSource to set
	 */
	public void setSpecimenSource(ConceptLight specimenSource) {
		this.specimenSource = specimenSource;
	}
	
	/**
	 * Gets the laterality
	 *
	 * @return the laterality
	 */
	public String getLaterality() {
		return laterality;
	}
	
	/**
	 * Sets the laterality
	 *
	 * @param laterality the laterality to set
	 */
	public void setLaterality(String laterality) {
		this.laterality = laterality;
	}
	
	/**
	 * Gets the clinicalHistory
	 *
	 * @return the clinicalHistory
	 */
	public String getClinicalHistory() {
		return clinicalHistory;
	}
	
	/**
	 * Sets the clinicalHistory
	 *
	 * @param clinicalHistory the clinicalHistory to set
	 */
	public void setClinicalHistory(String clinicalHistory) {
		this.clinicalHistory = clinicalHistory;
	}
	
	/**
	 * Gets the frequency
	 *
	 * @return the frequency
	 */
	public OrderFrequencyLight getFrequency() {
		return frequency;
	}
	
	/**
	 * Sets the frequency
	 *
	 * @param frequency the frequency to set
	 */
	public void setFrequency(OrderFrequencyLight frequency) {
		this.frequency = frequency;
	}
	
	/**
	 * Gets the numberOfRepeats
	 *
	 * @return the numberOfRepeats
	 */
	public Integer getNumberOfRepeats() {
		return numberOfRepeats;
	}
	
	/**
	 * Sets the numberOfRepeats
	 *
	 * @param numberOfRepeats the numberOfRepeats to set
	 */
	public void setNumberOfRepeats(Integer numberOfRepeats) {
		this.numberOfRepeats = numberOfRepeats;
	}
	
}
