package org.openmrs.eip.app.route.sender;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.openmrs.eip.app.management.entity.SenderRetryQueueItem;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.utils.Utils;

public final class SenderTestUtils {
	
	public static boolean hasRetryItem(String tableName, String id, String dest) {
		ProducerTemplate template = SyncContext.getBean(ProducerTemplate.class);
		final String type = SenderRetryQueueItem.class.getSimpleName();
		String query = "jpa:" + type + "?query=SELECT r FROM " + type + " r WHERE r.event.tableName IN ("
		        + Utils.getTablesInHierarchy(tableName) + ") AND r.event.primaryKeyId='" + id + "' AND r.route='" + dest
		        + "'";
		return template.requestBody(query, null, List.class).size() > 0;
	}
	
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
	
}
