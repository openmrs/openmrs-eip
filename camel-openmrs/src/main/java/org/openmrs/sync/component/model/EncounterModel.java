package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class EncounterModel extends BaseChangeableDataModel {

    private String encounterTypeUuid;

    private String patientUuid;

    private String locationUuid;

    private String formUuid;

    private LocalDateTime encounterDatetime;

    private String visitUuid;
}
