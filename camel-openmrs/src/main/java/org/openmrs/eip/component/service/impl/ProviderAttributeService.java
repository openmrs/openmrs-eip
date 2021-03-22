package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.ProviderAttribute;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.AttributeModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class ProviderAttributeService extends AbstractEntityService<ProviderAttribute, AttributeModel> {

    public ProviderAttributeService(final SyncEntityRepository<ProviderAttribute> repository,
                                    final EntityToModelMapper<ProviderAttribute, AttributeModel> entityToModelMapper,
                                    final ModelToEntityMapper<AttributeModel, ProviderAttribute> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.PROVIDER_ATTRIBUTE;
    }
}
