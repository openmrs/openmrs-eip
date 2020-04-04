package org.openmrs.sync.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "patient")
@PrimaryKeyJoinColumn(name = "patient_id")
public class PatientLight extends PersonLight {

    @NotNull
    @Column(name = "allergy_status")
    private String allergyStatus;

    @Column(name = "creator")
    private Long patientCreator;

    @Column(name = "date_created")
    private LocalDateTime patientDateCreated;
}
