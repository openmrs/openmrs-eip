package org.openmrs.eip.mysql.watcher;

import static org.openmrs.eip.mysql.watcher.WatcherTestConstants.URI_MOCK_EVENT_PROCESSOR;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.MapPropertySource;

/**
 * Test BeanPostProcessor that overrides watcher property values with test values e.g. the
 * WatcherConstants.PROP_URI_EVENT_PROCESSOR property value is overridden to set it to a mock
 * endpoint.
 */
public class WatcherPropertiesBeanPostProcessor implements BeanPostProcessor {
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (WatcherConstants.PROP_SOURCE_NAME.equals(beanName)) {
			MapPropertySource propSource = (MapPropertySource) bean;
			propSource.getSource().put(WatcherConstants.PROP_URI_EVENT_PROCESSOR, URI_MOCK_EVENT_PROCESSOR);
		}
		
		return bean;
	}
	
}
