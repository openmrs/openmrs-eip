package org.openmrs.sync.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.core.entity.light.AttributeTypeLight;
import org.openmrs.sync.core.entity.light.LightEntity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class Attribute<R extends LightEntity, T extends AttributeTypeLight> extends AuditableEntity {

    public abstract R getReferencedEntity();

    @NotNull
    @ManyToOne
    @JoinColumn(name = "attribute_type_id")
    private T attributeType;

    @NotNull
    @Column(name = "value_reference")
    private String valueReference;
}
