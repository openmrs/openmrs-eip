package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class EncounterProviderModel extends BaseChangeableDataModel {
    private String encounterUuid;
    private String providerUuid;
    private String encounterRoleUuid;
    
	public String getEncounterUuid() {
		return encounterUuid;
	}
	public void setEncounterUuid(String encounterUuid) {
		this.encounterUuid = encounterUuid;
	}
	public String getProviderUuid() {
		return providerUuid;
	}
	
	public void setProviderUuid(String providerUuid) {
		this.providerUuid = providerUuid;
	}
	
	public String getEncounterRoleUuid() {
		return encounterRoleUuid;
	}
	public void setEncounterRoleUuid(String encounterRoleUuid) {
		this.encounterRoleUuid = encounterRoleUuid;
	}
}
