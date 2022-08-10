package org.openmrs.eip.app.route.receiver;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.utils.Utils;

public final class ReceiverTestUtils {
	
	public static boolean hasRetryItem(Class<? extends BaseModel> modelClass, String uuid) {
		ProducerTemplate template = SyncContext.getBean(ProducerTemplate.class);
		final String type = ReceiverRetryQueueItem.class.getSimpleName();
		String query = "jpa:" + type + "?query=SELECT r FROM " + type + " r WHERE r.modelClassName IN ("
		        + Utils.getModelClassesInHierarchy(modelClass.getName()) + ") AND r.identifier='" + uuid + "'";
		return template.requestBody(query, null, List.class).size() > 0;
	}
	
	public static boolean hasConflict(Class<? extends BaseModel> modelClass, String uuid) {
		ProducerTemplate template = SyncContext.getBean(ProducerTemplate.class);
		final String type = ConflictQueueItem.class.getSimpleName();
		String query = "jpa:" + type + "?query=SELECT c FROM " + type + " c WHERE c.modelClassName IN ("
		        + Utils.getModelClassesInHierarchy(modelClass.getName()) + ") AND c.identifier='" + uuid
		        + "' AND c.resolved = false";
		return template.requestBody(query, null, List.class).size() > 0;
	}
	
}
