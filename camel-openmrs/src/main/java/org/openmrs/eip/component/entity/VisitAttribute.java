package org.openmrs.eip.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.entity.light.VisitAttributeTypeLight;
import org.openmrs.eip.component.entity.light.VisitLight;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "visit_attribute")
@AttributeOverride(name = "id", column = @Column(name = "visit_attribute_id"))
public class VisitAttribute extends Attribute<VisitAttributeTypeLight> {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "visit_id")
	private VisitLight referencedEntity;
}
