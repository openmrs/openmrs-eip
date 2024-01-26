package org.openmrs.eip.app.route.sender;

import static org.openmrs.eip.app.sender.SenderConstants.PROP_SENDER_ID;

import org.openmrs.eip.app.management.entity.sender.DebeziumEvent;
import org.openmrs.eip.app.route.BaseRouteTest;
import org.openmrs.eip.app.sender.SenderConstants;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.entity.Event;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles(SyncProfiles.SENDER)
@TestPropertySource(properties = PROP_SENDER_ID + "=")
@TestPropertySource(properties = SenderConstants.PROP_ACTIVEMQ_ENDPOINT + "=")
public abstract class BaseSenderRouteTest extends BaseRouteTest {
	
	protected Event createEvent(String table, String pkId, String identifier, String op) {
		Event event = new Event();
		event.setTableName(table);
		event.setPrimaryKeyId(pkId);
		event.setIdentifier(identifier);
		event.setOperation(op);
		event.setSnapshot(false);
		return event;
	}
	
	protected DebeziumEvent createDebeziumEvent(String table, String pkId, String uuid, String op) {
		DebeziumEvent dbzmEvent = new DebeziumEvent();
		dbzmEvent.setEvent(createEvent(table, pkId, uuid, op));
		return dbzmEvent;
	}
	
	@Override
	public String getAppFolderName() {
		return "sender";
	}
	
}
