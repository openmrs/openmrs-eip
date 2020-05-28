package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.OrderFrequency;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.OrderFrequencyModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class OrderFrequencyService extends AbstractEntityService<OrderFrequency, OrderFrequencyModel> {

    public OrderFrequencyService(final SyncEntityRepository<OrderFrequency> repository,
                                 final EntityToModelMapper<OrderFrequency, OrderFrequencyModel> entityToModelMapper,
                                 final ModelToEntityMapper<OrderFrequencyModel, OrderFrequency> modelToEntityMapper) {

        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.ORDER_FREQUENCY;
    }

}
