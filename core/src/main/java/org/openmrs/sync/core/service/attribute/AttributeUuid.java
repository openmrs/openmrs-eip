package org.openmrs.sync.core.service.attribute;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class AttributeUuid {

    private String key;

    private String uuid;
}
