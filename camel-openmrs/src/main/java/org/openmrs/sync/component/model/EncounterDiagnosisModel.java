package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EncounterDiagnosisModel extends BaseChangeableDataModel {

    private String diagnosisCodedUuid;

    private String diagnosisNonCoded;

    private String diagnosisCodedNameUuid;

    private String encounterUuid;

    private String patientUuid;

    private String conditionUuid;

    private String certainty;

    private int rank;
}
