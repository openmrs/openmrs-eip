package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ProviderModel extends BaseChangeableMetadataModel {
	
    private String name;
	 
    private String identifier;
	 
    private String providerRoleUuid;

    private String roleUuid;
    
    private String specialityUuid;

    private String personUuid;

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

	public String getPersonUuid() {
		return personUuid;
	}

	public void setPersonUuid(String personUuid) {
		this.personUuid = personUuid;
	}

	public String getProviderRoleUuid() {
		return providerRoleUuid;
	}

	public void setProviderRoleUuid(String providerRoleUuid) {
		this.providerRoleUuid = providerRoleUuid;
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
}
