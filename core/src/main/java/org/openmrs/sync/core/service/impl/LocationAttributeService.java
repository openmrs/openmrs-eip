package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.LocationAttribute;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.AttributeModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.springframework.stereotype.Service;

@Service
public class LocationAttributeService extends AbstractEntityService<LocationAttribute, AttributeModel> {

    public LocationAttributeService(final SyncEntityRepository<LocationAttribute> repository,
                                    final EntityMapper<LocationAttribute, AttributeModel> mapper) {
        super(repository, mapper);
    }

    @Override
    public EntityNameEnum getEntityName() {
        return EntityNameEnum.LOCATION_ATTRIBUTE;
    }
}
