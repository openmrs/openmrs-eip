package org.openmrs.eip.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.entity.light.LocationLight;
import org.openmrs.eip.component.entity.light.PatientIdentifierTypeLight;
import org.openmrs.eip.component.entity.light.PatientLight;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "patient_identifier")
@AttributeOverride(name = "id", column = @Column(name = "patient_identifier_id"))
public class PatientIdentifier extends BaseChangeableDataEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientLight patient;

    @NotNull
    @Column(name = "identifier")
    private String identifier;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "identifier_type")
    private PatientIdentifierTypeLight patientIdentifierType;

    @NotNull
    @Column(name = "preferred")
    private boolean preferred;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationLight location;
}
