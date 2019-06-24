package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.ProviderAttribute;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.AttributeModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.springframework.stereotype.Service;

@Service
public class ProviderAttributeService extends AbstractEntityService<ProviderAttribute, AttributeModel> {

    public ProviderAttributeService(final SyncEntityRepository<ProviderAttribute> repository,
                                    final EntityMapper<ProviderAttribute, AttributeModel> mapper) {
        super(repository, mapper);
    }

    @Override
    public EntityNameEnum getEntityName() {
        return EntityNameEnum.PROVIDER_ATTRIBUTE;
    }
}
