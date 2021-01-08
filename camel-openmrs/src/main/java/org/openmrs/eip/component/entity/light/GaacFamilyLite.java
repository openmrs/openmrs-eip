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
@Table(name = "gaac_family")
@AttributeOverride(name = "id", column = @Column(name = "family_id"))
public class GaacFamilyLite extends VoidableLightEntity {
    @Column(name = "family_identifier")
    private String familyIdentifier;
}