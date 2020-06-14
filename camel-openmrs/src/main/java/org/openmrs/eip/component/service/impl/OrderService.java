package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.Order;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.OrderModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
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
