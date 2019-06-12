package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.OrderTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderTypeLightService extends AbstractLightService<OrderTypeLight> {

    public OrderTypeLightService(final OpenMrsRepository<OrderTypeLight> repository) {
        super(repository);
    }

    @Override
    protected OrderTypeLight getFakeEntity(final String uuid, final List<AttributeUuid> uuids) {
        OrderTypeLight orderType = new OrderTypeLight();
        orderType.setUuid(uuid);
        orderType.setDateCreated(DEFAULT_DATE);
        orderType.setCreator(DEFAULT_USER_ID);
        orderType.setName(DEFAULT_STRING);
        orderType.setJavaClassName(DEFAULT_STRING);
        return orderType;
    }
}
