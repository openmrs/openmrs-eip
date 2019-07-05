package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.OrderTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class OrderTypeLightService extends AbstractLightService<OrderTypeLight> {

    public OrderTypeLightService(final OpenMrsRepository<OrderTypeLight> repository) {
        super(repository);
    }

    @Override
    protected OrderTypeLight createPlaceholderEntity(final String uuid) {
        OrderTypeLight orderType = new OrderTypeLight();
        orderType.setDateCreated(DEFAULT_DATE);
        orderType.setCreator(DEFAULT_USER_ID);
        orderType.setName(DEFAULT_STRING);
        orderType.setJavaClassName(DEFAULT_STRING);
        return orderType;
    }
}
