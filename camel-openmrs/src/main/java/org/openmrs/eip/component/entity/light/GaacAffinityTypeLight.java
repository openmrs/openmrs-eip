package org.openmrs.eip.component.entity.light;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "gaac_affinity_type")
@AttributeOverride(name = "id", column = @Column(name = "gaac_affinity_type_id"))
public class GaacAffinityTypeLight extends RetireableLightEntity {

    @NotNull
    @Column(name = "name")
    private String name;
}
