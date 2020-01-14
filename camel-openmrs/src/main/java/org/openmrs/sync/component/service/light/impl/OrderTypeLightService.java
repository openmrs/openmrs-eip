package org.openmrs.sync.component.service.light.impl;

import org.openmrs.sync.component.entity.light.OrderTypeLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.openmrs.sync.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class OrderTypeLightService extends AbstractLightService<OrderTypeLight> {

    public OrderTypeLightService(final OpenmrsRepository<OrderTypeLight> repository) {
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
