package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.LocationAttribute;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.AttributeModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class LocationAttributeService extends AbstractEntityService<LocationAttribute, AttributeModel> {

    public LocationAttributeService(final SyncEntityRepository<LocationAttribute> repository,
                                    final EntityToModelMapper<LocationAttribute, AttributeModel> entityToModelMapper,
                                    final ModelToEntityMapper<AttributeModel, LocationAttribute> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.LOCATION_ATTRIBUTE;
    }
}
