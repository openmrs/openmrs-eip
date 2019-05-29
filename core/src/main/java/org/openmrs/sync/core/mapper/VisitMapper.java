package org.openmrs.sync.core.mapper;

import org.mapstruct.Mapper;
import org.openmrs.sync.core.entity.Visit;
import org.openmrs.sync.core.model.VisitModel;

@Mapper(componentModel = "spring")
public abstract class VisitMapper implements EntityMapper<Visit, VisitModel> {

    @Override
    public VisitModel entityToModel(final Visit entity) {
        return null;
    }

    @Override
    public Visit modelToEntity(final VisitModel model) {
        return null;
    }
}
