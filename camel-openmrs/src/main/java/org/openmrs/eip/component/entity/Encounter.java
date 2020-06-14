package org.openmrs.eip.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.entity.light.EncounterTypeLight;
import org.openmrs.eip.component.entity.light.FormLight;
import org.openmrs.eip.component.entity.light.LocationLight;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.VisitLight;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "encounter")
@AttributeOverride(name = "id", column = @Column(name = "encounter_id"))
public class Encounter extends AuditableEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "encounter_type")
    private EncounterTypeLight encounterType;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientLight patient;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationLight location;

    @ManyToOne
    @JoinColumn(name = "form_id")
    private FormLight form;

    @NotNull
    @Column(name = "encounter_datetime")
    private LocalDateTime encounterDatetime;

    @ManyToOne
    @JoinColumn(name = "visit_id")
    private VisitLight visit;
}
