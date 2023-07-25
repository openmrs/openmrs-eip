package org.openmrs.eip.app.management.entity.sender;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage.SenderSyncMessageStatus;

public class SenderSyncMessageTest {
	
	@Test
	public void markAsSent_shouldSetStatusToSentAndSetDateSent() {
		SenderSyncMessage msg = new SenderSyncMessage();
		Assert.assertEquals(SenderSyncMessageStatus.NEW, msg.getStatus());
		Assert.assertNull(msg.getDateSent());
		LocalDateTime dateSent = LocalDateTime.now();
		
		msg.markAsSent(dateSent);
		
		Assert.assertEquals(SenderSyncMessageStatus.SENT, msg.getStatus());
		Assert.assertEquals(dateSent.atZone(ZoneId.systemDefault()).toInstant(), msg.getDateSent().toInstant());
	}
	
}
