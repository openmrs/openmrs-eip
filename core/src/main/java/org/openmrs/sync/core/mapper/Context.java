package org.openmrs.sync.core.mapper;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.model.BaseModel;

@Value
@Builder
@EqualsAndHashCode
public class Context {

    private BaseEntity entity;

    private BaseModel model;
}
