package org.openmrs.eip.component.model;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class GaacModel extends BaseChangeableDataModel {
	private String name;
	private String description;
	protected LocalDateTime startDate;
	protected LocalDateTime endDate;
	private String focalPatientUuid;
	private String affinityTypeUuid;
	private String locationUuid;
	private Boolean crumbled;
	private String reasonCrumbled;
	protected LocalDateTime dateCrumbled;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LocalDateTime getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}
	public LocalDateTime getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}
	public String getFocalPatientUuid() {
		return focalPatientUuid;
	}
	public void setFocalPatientUuid(String focalPatientUuid) {
		this.focalPatientUuid = focalPatientUuid;
	}
	public String getLocationUuid() {
		return locationUuid;
	}
	public void setLocationUuid(String locationUuid) {
		this.locationUuid = locationUuid;
	}
		
	public String getReasonCrumbled() {
		return reasonCrumbled;
	}
	public void setReasonCrumbled(String reasonCrumbled) {
		this.reasonCrumbled = reasonCrumbled;
	}
	public LocalDateTime getDateCrumbled() {
		return dateCrumbled;
	}
	public void setDateCrumbled(LocalDateTime dateCrumbled) {
		this.dateCrumbled = dateCrumbled;
	}
	public String getAffinityTypeUuid() {
		return affinityTypeUuid;
	}
	public void setAffinityTypeUuid(String affinityTypeUuid) {
		this.affinityTypeUuid = affinityTypeUuid;
	}
	public Boolean getCrumbled() {
		return crumbled;
	}
	public void setCrumbled(Boolean crumbled) {
		this.crumbled = crumbled;
	}	 
}
