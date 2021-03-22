package org.openmrs.eip.component.model;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ProviderModel extends BaseModel {
    
	private String name;
	 
    private String identifier;

    private String roleUuid;
    
    private String specialityUuid;

    private String personUuid;

    private boolean retired;

    private String retiredByUuid;

    private LocalDateTime dateRetired;

    private String retiredReason;

    private String changedByUuid;

    private LocalDateTime dateChanged;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getRoleUuid() {
		return roleUuid;
	}

	public void setRoleUuid(String roleUuid) {
		this.roleUuid = roleUuid;
	}

	public String getSpecialityUuid() {
		return specialityUuid;
	}

	public void setSpecialityUuid(String specialityUuid) {
		this.specialityUuid = specialityUuid;
	}

	public String getPersonUuid() {
		return personUuid;
	}

	public void setPersonUuid(String personUuid) {
		this.personUuid = personUuid;
	}

	public boolean isRetired() {
		return retired;
	}

	public void setRetired(boolean retired) {
		this.retired = retired;
	}

	public String getRetiredByUuid() {
		return retiredByUuid;
	}

	public void setRetiredByUuid(String retiredByUuid) {
		this.retiredByUuid = retiredByUuid;
	}

	public LocalDateTime getDateRetired() {
		return dateRetired;
	}

	public void setDateRetired(LocalDateTime dateRetired) {
		this.dateRetired = dateRetired;
	}

	public String getRetiredReason() {
		return retiredReason;
	}

	public void setRetiredReason(String retiredReason) {
		this.retiredReason = retiredReason;
	}

	public String getChangedByUuid() {
		return changedByUuid;
	}

	public void setChangedByUuid(String changedByUuid) {
		this.changedByUuid = changedByUuid;
	}

	public LocalDateTime getDateChanged() {
		return dateChanged;
	}

	public void setDateChanged(LocalDateTime dateChanged) {
		this.dateChanged = dateChanged;
	}

}
