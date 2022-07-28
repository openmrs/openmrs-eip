package org.openmrs.eip.app.route;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.openmrs.eip.component.SyncContext;

public final class TestUtils {
	
	public static <T extends AbstractEntity> T getEntity(Class<T> clazz, Long id) {
		ProducerTemplate template = SyncContext.getBean(ProducerTemplate.class);
		final String classname = clazz.getSimpleName();
		String query = "jpa:" + classname + "?query=SELECT i FROM " + classname + " i WHERE i.id = " + id;
		List<T> matches = template.requestBody(query, null, List.class);
		if (matches.size() == 1) {
			return matches.get(0);
		}
		
		return null;
	}
	
	public static <T extends AbstractEntity> List<T> getEntities(Class<T> clazz) {
		ProducerTemplate t = SyncContext.getBean(ProducerTemplate.class);
		final String classname = clazz.getSimpleName();
		return t.requestBody("jpa:" + classname + "?query=SELECT i FROM " + classname + " i", null, List.class);
	}
	
	public static <T extends AbstractEntity> void saveEntity(T entity) {
		ProducerTemplate t = SyncContext.getBean(ProducerTemplate.class);
		t.sendBody("jpa:" + entity.getClass().getSimpleName() + "?usePersist=true", entity);
	}
	
}
