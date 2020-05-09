package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class VisitModel extends BaseChangeableDataModel {

    private String patientUuid;

    private String visitTypeUuid;

    private LocalDateTime dateStarted;

    private LocalDateTime dateStopped;

    private String indicationConceptUuid;

    private String locationUuid;
}
