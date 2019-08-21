package org.openmrs.sync.common.model.sync;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConceptModel extends BaseModel {

    private String datatypeUuid;

    private String conceptClassUuid;

    private String shortName;

    private String description;

    private String formText;

    private String version;

    private boolean isSet;
}
