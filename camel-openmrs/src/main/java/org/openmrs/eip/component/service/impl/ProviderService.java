package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.Provider;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.ProviderModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class ProviderService extends AbstractEntityService<Provider, ProviderModel> {

    public ProviderService(final SyncEntityRepository<Provider> repository, final EntityToModelMapper<Provider, ProviderModel> entityToModelMapper, final ModelToEntityMapper<ProviderModel, Provider> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.PROVIDER;
    }
}
