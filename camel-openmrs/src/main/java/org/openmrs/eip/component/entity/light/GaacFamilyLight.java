package org.openmrs.eip.component.entity.light;

import java.time.LocalDateTime;

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
@Table(name = "gaac_family")
@AttributeOverride(name = "id", column = @Column(name = "family_id"))
public class GaacFamilyLight extends VoidableLightEntity {
    @Column(name = "family_identifier")
    private String familyIdentifier;
    
    @NotNull
    @Column(name = "start_date")
    protected LocalDateTime startDate;
}