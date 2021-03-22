package org.openmrs.eip.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "encounter_type")
@AttributeOverride(name = "id", column = @Column(name = "encounter_type_id"))
public class EncounterTypeLight extends RetireableLightEntity {

    @Column(name = "name")
    private String name;
}
