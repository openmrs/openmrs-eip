package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.OrderFrequencyModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.entity.OrderFrequency;
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
