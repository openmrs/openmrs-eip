package org.openmrs.sync.common.model.sync;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientModel extends PersonModel {

    private String allergyStatus;

    private String patientCreatorUuid;

    private LocalDateTime patientDateCreated;

    private String patientChangedByUuid;

    private LocalDateTime patientDateChanged;

    private boolean patientVoided;

    private String patientVoidedByUuid;

    private LocalDateTime patientDateVoided;

    private String patientVoidReason;

    private boolean deathdateEstimated;

    private LocalTime birthtime;
}
