package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class EncounterProviderModel extends BaseChangeableDataModel {
    private String encounterUuid;
    private String providerUuid;
    private int encounterRoleId;
    
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
	public int getEncounterRoleId() {
		return encounterRoleId;
	}
	public void setEncounterRoleId(int encounterRoleId) {
		this.encounterRoleId = encounterRoleId;
	}
}
