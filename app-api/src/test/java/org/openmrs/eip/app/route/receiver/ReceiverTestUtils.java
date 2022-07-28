package org.openmrs.eip.app.route.receiver;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.utils.Utils;

public final class ReceiverTestUtils {
	
	public static boolean hasRetryItem(String tableName, String uuid) {
		ProducerTemplate template = SyncContext.getBean(ProducerTemplate.class);
		final String type = ReceiverRetryQueueItem.class.getSimpleName();
		String query = "jpa:" + type + "?query=SELECT r FROM " + type + " r WHERE r.modelClassName IN ("
		        + Utils.getModelClassesInHierarchy(tableName) + ") AND r.identifier='" + uuid + "'";
		return template.requestBody(query, null, List.class).size() > 0;
	}
	
}
