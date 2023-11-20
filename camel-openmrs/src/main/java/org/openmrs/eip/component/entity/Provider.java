package org.openmrs.eip.component.entity;

import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.PersonLight;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "provider")
@AttributeOverride(name = "id", column = @Column(name = "provider_id"))
public class Provider extends BaseChangeableMetaDataEntity {
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "identifier")
	private String identifier;
	
	/*@ManyToOne
	@JoinColumn(name = "provider_role_id")
	private ProviderManagementProviderRoleLight providerRole;*/
	
	@ManyToOne
	@JoinColumn(name = "role_id")
	private ConceptLight role;
	
	@ManyToOne
	@JoinColumn(name = "speciality_id")
	private ConceptLight speciality;
	
	@OneToOne
	@JoinColumn(name = "person_id")
	private PersonLight person;
	
}
