package org.openmrs.sync.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.component.entity.light.LocationLight;
import org.openmrs.sync.component.entity.light.PatientIdentifierTypeLight;
import org.openmrs.sync.component.entity.light.PatientLight;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "patient_identifier")
@AttributeOverride(name = "id", column = @Column(name = "patient_identifier_id"))
public class PatientIdentifier extends AuditableEntity {

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
