package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConceptModel extends BaseChangeableMetadataModel {

    private String datatypeUuid;

    private String conceptClassUuid;

    private String shortName;

    private String description;

    private String formText;

    private String version;

    private boolean isSet;
}
