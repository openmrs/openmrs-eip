package org.openmrs.eip.app.sender;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.powermock.modules.junit4.PowerMockRunner;

import jakarta.jms.Session;
import jakarta.jms.TextMessage;

@RunWith(PowerMockRunner.class)
public class SyncMessageCreatorTest {
	
	@Mock
	private Session mockSession;
	
	@Mock
	private TextMessage mockMsg;
	
	@Test
	public void createMessage_shouldCreateJmsMessage() throws Exception {
		final String body = "test_body";
		final String siteId = "test_site_id";
		SyncMessageCreator creator = new SyncMessageCreator(body, siteId);
		Mockito.when(mockSession.createTextMessage(body)).thenReturn(mockMsg);
		
		TextMessage msg = (TextMessage) creator.createMessage(mockSession);
		
		assertEquals(msg, mockMsg);
		Mockito.verify(msg).setStringProperty(SyncConstants.JMS_HEADER_SITE, siteId);
		Mockito.verify(msg).setStringProperty(SyncConstants.JMS_HEADER_TYPE, JmsMessage.MessageType.SYNC.name());
		Mockito.verify(msg).setStringProperty(eq(SyncConstants.JMS_HEADER_MSG_ID), ArgumentMatchers.anyString());
		Mockito.verify(msg).setStringProperty(eq(SyncConstants.JMS_HEADER_VERSION), eq(AppUtils.getVersion()));
	}
	
}
