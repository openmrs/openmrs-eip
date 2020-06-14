package org.openmrs.eip.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class AttributeTypeLight extends RetireableLightEntity {

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "min_occurs")
    private long minOccurs;
}
