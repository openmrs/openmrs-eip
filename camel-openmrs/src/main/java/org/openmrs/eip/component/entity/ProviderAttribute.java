package org.openmrs.eip.component.entity;

import org.openmrs.eip.component.entity.light.ProviderAttributeTypeLight;
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
@Table(name = "provider_attribute")
@AttributeOverride(name = "id", column = @Column(name = "provider_attribute_id"))
public class ProviderAttribute extends Attribute<ProviderAttributeTypeLight> {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "provider_id")
	private ProviderLight referencedEntity;
}
