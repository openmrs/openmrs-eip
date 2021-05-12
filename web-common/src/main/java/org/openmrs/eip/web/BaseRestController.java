package org.openmrs.eip.web;

import static org.apache.camel.impl.engine.DefaultFluentProducerTemplate.on;
import static org.openmrs.eip.web.RestConstants.DEFAULT_MAX_COUNT;

import java.util.List;

import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseRestController {
	
	protected static final Logger log = LoggerFactory.getLogger(BaseRestController.class);
	
	@Autowired
	protected CamelContext camelContext;
	
	public List<Object> doGetAll() {
		return on(camelContext)
		        .to("jpa:" + getName() + "?query=SELECT c FROM " + getName() + " c &maximumResults=" + DEFAULT_MAX_COUNT)
		        .request(List.class);
	}
	
	public Object doGet(Integer id) {
		return on(camelContext).to("jpa:" + getName() + "?query=SELECT c FROM " + getName() + " c WHERE c.id = " + id)
		        .request(getClazz());
	}
	
	public void doDelete(Integer id) {
		on(camelContext).to("jpa:" + getName() + "?query=DELETE FROM " + getName() + " WHERE id = " + id).request();
	}
	
	public String getName() {
		return getClazz().getSimpleName();
	}
	
	public abstract Class<?> getClazz();
	
}
