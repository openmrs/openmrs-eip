package org.openmrs.eip.app.route.sender;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.SenderRetryQueueItem;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.utils.Utils;

public final class SenderTestUtils {
	
	public static boolean hasRetryItem(String tableName, String id) {
		ProducerTemplate template = SyncContext.getBean(ProducerTemplate.class);
		final String type = SenderRetryQueueItem.class.getSimpleName();
		String query = "jpa:" + type + "?query=SELECT r FROM " + type + " r WHERE r.event.tableName IN ("
		        + Utils.getTablesInHierarchy(tableName) + ") AND r.event.primaryKeyId='" + id + "'";
		return template.requestBody(query, null, List.class).size() > 0;
	}
	
}
