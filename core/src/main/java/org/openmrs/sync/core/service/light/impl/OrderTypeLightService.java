package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.OrderTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightServiceNoContext;
import org.springframework.stereotype.Service;

@Service
public class OrderTypeLightService extends AbstractLightServiceNoContext<OrderTypeLight> {

    public OrderTypeLightService(final OpenMrsRepository<OrderTypeLight> repository) {
        super(repository);
    }

    @Override
    protected OrderTypeLight getShadowEntity(final String uuid) {
        OrderTypeLight orderType = new OrderTypeLight();
        orderType.setUuid(uuid);
        orderType.setDateCreated(DEFAULT_DATE);
        orderType.setCreator(DEFAULT_USER_ID);
        orderType.setName(DEFAULT_STRING);
        orderType.setJavaClassName(DEFAULT_STRING);
        return orderType;
    }
}
