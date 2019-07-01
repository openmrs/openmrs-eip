package org.openmrs.sync.core.mapper;

import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.entity.light.LightEntity;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModelToEntityMapper {

    private List<LightServiceNoContext<? extends LightEntity>> services;

    public ModelToEntityMapper(final List<LightServiceNoContext<? extends LightEntity>> services) {
        this.services = services;
    }

    public BaseEntity modelToEntity(final BaseModel model) {
        return null;
    }
}
