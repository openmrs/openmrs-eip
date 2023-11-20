package org.openmrs.eip.component.entity;

import org.openmrs.eip.component.entity.light.AttributeTypeLight;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class Attribute<T extends AttributeTypeLight> extends BaseChangeableDataEntity {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "attribute_type_id")
	private T attributeType;
	
	@NotNull
	@Column(name = "value_reference")
	private String valueReference;
}
