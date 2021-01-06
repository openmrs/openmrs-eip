package org.openmrs.eip.component.entity.light;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "gaac")
@AttributeOverride(name = "id", column = @Column(name = "gaac_id"))
public class GaacLite extends VoidableLightEntity {
    @Column(name = "name")
    private String name;
}
