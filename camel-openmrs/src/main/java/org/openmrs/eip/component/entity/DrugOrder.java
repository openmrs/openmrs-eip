package org.openmrs.eip.component.entity;

import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.DrugLight;
import org.openmrs.eip.component.entity.light.OrderFrequencyLight;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "drug_order")
@PrimaryKeyJoinColumn(name = "order_id")
public class DrugOrder extends Order {
	
	@Column(name = "dose")
	private Double dose;
	
	@ManyToOne
	@JoinColumn(name = "dose_units")
	private ConceptLight doseUnits;
	
	@Column(name = "as_needed")
	private Boolean asNeeded;
	
	@Column(name = "quantity")
	private Double quantity;
	
	@ManyToOne
	@JoinColumn(name = "quantity_units")
	private ConceptLight quantityUnits;
	
	@ManyToOne
	@JoinColumn(name = "drug_inventory_id")
	private DrugLight drug;
	
	@Column(name = "as_needed_condition")
	private String asNeededCondition;
	
	@Column(name = "num_refills")
	private Integer numRefills;
	
	@Column(name = "dosing_instructions")
	private String dosingInstructions;
	
	@Column(name = "duration")
	private Integer duration;
	
	@ManyToOne
	@JoinColumn(name = "duration_units")
	private ConceptLight durationUnits;
	
	@ManyToOne
	@JoinColumn(name = "route")
	private ConceptLight route;
	
	@Column(name = "brand_name")
	private String brandName;
	
	@NotNull
	@Column(name = "dispense_as_written", nullable = false)
	private Boolean dispenseAsWritten;
	
	@Column(name = "drug_non_coded")
	private String drugNonCoded;
	
	@ManyToOne
	@JoinColumn(name = "frequency")
	private OrderFrequencyLight frequency;
	
	@Column(name = "dosing_type")
	private String dosingType;
	
	/**
	 * Gets the dose
	 *
	 * @return the dose
	 */
	public Double getDose() {
		return dose;
	}
	
	/**
	 * Sets the dose
	 *
	 * @param dose the dose to set
	 */
	public void setDose(Double dose) {
		this.dose = dose;
	}
	
	/**
	 * Gets the doseUnits
	 *
	 * @return the doseUnits
	 */
	public ConceptLight getDoseUnits() {
		return doseUnits;
	}
	
	/**
	 * Sets the doseUnits
	 *
	 * @param doseUnits the doseUnits to set
	 */
	public void setDoseUnits(ConceptLight doseUnits) {
		this.doseUnits = doseUnits;
	}
	
	/**
	 * Gets the asNeeded
	 *
	 * @return the asNeeded
	 */
	public Boolean getAsNeeded() {
		return asNeeded;
	}
	
	/**
	 * Sets the asNeeded
	 *
	 * @param asNeeded the asNeeded to set
	 */
	public void setAsNeeded(Boolean asNeeded) {
		this.asNeeded = asNeeded;
	}
	
	/**
	 * Gets the quantity
	 *
	 * @return the quantity
	 */
	public Double getQuantity() {
		return quantity;
	}
	
	/**
	 * Sets the quantity
	 *
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	
	/**
	 * Gets the quantityUnits
	 *
	 * @return the quantityUnits
	 */
	public ConceptLight getQuantityUnits() {
		return quantityUnits;
	}
	
	/**
	 * Sets the quantityUnits
	 *
	 * @param quantityUnits the quantityUnits to set
	 */
	public void setQuantityUnits(ConceptLight quantityUnits) {
		this.quantityUnits = quantityUnits;
	}
	
	/**
	 * Gets the drug
	 *
	 * @return the drug
	 */
	public DrugLight getDrug() {
		return drug;
	}
	
	/**
	 * Sets the drug
	 *
	 * @param drug the drug to set
	 */
	public void setDrug(DrugLight drug) {
		this.drug = drug;
	}
	
	/**
	 * Gets the asNeededCondition
	 *
	 * @return the asNeededCondition
	 */
	public String getAsNeededCondition() {
		return asNeededCondition;
	}
	
	/**
	 * Sets the asNeededCondition
	 *
	 * @param asNeededCondition the asNeededCondition to set
	 */
	public void setAsNeededCondition(String asNeededCondition) {
		this.asNeededCondition = asNeededCondition;
	}
	
	/**
	 * Gets the numRefills
	 *
	 * @return the numRefills
	 */
	public Integer getNumRefills() {
		return numRefills;
	}
	
	/**
	 * Sets the numRefills
	 *
	 * @param numRefills the numRefills to set
	 */
	public void setNumRefills(Integer numRefills) {
		this.numRefills = numRefills;
	}
	
	/**
	 * Gets the dosingInstructions
	 *
	 * @return the dosingInstructions
	 */
	public String getDosingInstructions() {
		return dosingInstructions;
	}
	
	/**
	 * Sets the dosingInstructions
	 *
	 * @param dosingInstructions the dosingInstructions to set
	 */
	public void setDosingInstructions(String dosingInstructions) {
		this.dosingInstructions = dosingInstructions;
	}
	
	/**
	 * Gets the duration
	 *
	 * @return the duration
	 */
	public Integer getDuration() {
		return duration;
	}
	
	/**
	 * Sets the duration
	 *
	 * @param duration the duration to set
	 */
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	
	/**
	 * Gets the durationUnits
	 *
	 * @return the durationUnits
	 */
	public ConceptLight getDurationUnits() {
		return durationUnits;
	}
	
	/**
	 * Sets the durationUnits
	 *
	 * @param durationUnits the durationUnits to set
	 */
	public void setDurationUnits(ConceptLight durationUnits) {
		this.durationUnits = durationUnits;
	}
	
	/**
	 * Gets the route
	 *
	 * @return the route
	 */
	public ConceptLight getRoute() {
		return route;
	}
	
	/**
	 * Sets the route
	 *
	 * @param route the route to set
	 */
	public void setRoute(ConceptLight route) {
		this.route = route;
	}
	
	/**
	 * Gets the brandName
	 *
	 * @return the brandName
	 */
	public String getBrandName() {
		return brandName;
	}
	
	/**
	 * Sets the brandName
	 *
	 * @param brandName the brandName to set
	 */
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	/**
	 * Gets the dispenseAsWritten
	 *
	 * @return the dispenseAsWritten
	 */
	public Boolean getDispenseAsWritten() {
		return dispenseAsWritten;
	}
	
	/**
	 * Sets the dispenseAsWritten
	 *
	 * @param dispenseAsWritten the dispenseAsWritten to set
	 */
	public void setDispenseAsWritten(Boolean dispenseAsWritten) {
		this.dispenseAsWritten = dispenseAsWritten;
	}
	
	/**
	 * Gets the drugNonCoded
	 *
	 * @return the drugNonCoded
	 */
	public String getDrugNonCoded() {
		return drugNonCoded;
	}
	
	/**
	 * Sets the drugNonCoded
	 *
	 * @param drugNonCoded the drugNonCoded to set
	 */
	public void setDrugNonCoded(String drugNonCoded) {
		this.drugNonCoded = drugNonCoded;
	}
	
	@Override
	public boolean wasModifiedAfter(BaseEntity entity) {
		return false;
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
	 * Gets the dosingType
	 *
	 * @return the dosingType
	 */
	public String getDosingType() {
		return dosingType;
	}
	
	/**
	 * Sets the dosingType
	 *
	 * @param dosingType the dosingType to set
	 */
	public void setDosingType(String dosingType) {
		this.dosingType = dosingType;
	}
	
}
