package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AllergyModel extends BaseChangeableDataModel {

    private String patientUuid;

    private String severityConceptUuid;

    private String codedAllergenUuid;

    private String nonCodedAllergen;

    private String allergenType;

    private String comments;
}
