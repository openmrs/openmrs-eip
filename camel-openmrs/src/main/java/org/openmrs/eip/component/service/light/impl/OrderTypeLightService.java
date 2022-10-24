package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.OrderTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
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
		orderType.setCreator(SyncContext.getAppUser().getId());
		orderType.setName(DEFAULT_STRING);
		orderType.setJavaClassName(DEFAULT_STRING);
		return orderType;
	}
}
