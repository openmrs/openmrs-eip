package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.Order;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.OrderModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class OrderService extends AbstractEntityService<Order, OrderModel> {

    public OrderService(final SyncEntityRepository<Order> repository,
                        final EntityToModelMapper<Order, OrderModel> entityToModelMapper,
                        final ModelToEntityMapper<OrderModel, Order> modelToEntityMapper) {

        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.ORDERS;
    }
}
