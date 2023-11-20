package org.openmrs.eip.component.entity;

import org.openmrs.eip.component.entity.light.EncounterLight;
import org.openmrs.eip.component.entity.light.EncounterRoleLight;
import org.openmrs.eip.component.entity.light.ProviderLight;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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
