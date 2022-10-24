package org.openmrs.eip.component.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.openmrs.eip.component.entity.light.EncounterLight;
import org.openmrs.eip.component.entity.light.EncounterRoleLight;
import org.openmrs.eip.component.entity.light.ProviderLight;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "encounter_provider")
@AttributeOverride(name = "id", column = @Column(name = "encounter_provider_id"))
public class EncounterProvider extends BaseChangeableDataEntity {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "encounter_role_id")
	private EncounterRoleLight encounterRole;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "encounter_id")
	private EncounterLight encounter;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "provider_id")
	private ProviderLight provider;
}
