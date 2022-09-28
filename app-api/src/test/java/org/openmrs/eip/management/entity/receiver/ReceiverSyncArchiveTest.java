package org.openmrs.eip.management.entity.receiver;

import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.component.model.PersonModel;
import org.springframework.beans.BeanUtils;

public class ReceiverSyncArchiveTest {
	
	@Test
	public void shouldCreateAnReceiverArchiveFromASyncMessage() throws Exception {
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(SyncMessage.class);
		assertEquals(descriptors.length, BeanUtils.getPropertyDescriptors(ReceiverSyncArchive.class).length);
		SyncMessage syncMessage = new SyncMessage();
		syncMessage.setId(1L);
		syncMessage.setDateCreated(new Date());
		syncMessage.setIdentifier("uuid");
		syncMessage.setEntityPayload("payload");
		syncMessage.setModelClassName(PersonModel.class.getName());
		syncMessage.setSite(new SiteInfo());
		syncMessage.setSnapshot(true);
		syncMessage.setMessageUuid("message-uuid");
		syncMessage.setDateSentBySender(LocalDateTime.now());
		
		ReceiverSyncArchive archive = new ReceiverSyncArchive(syncMessage);
		
		Assert.assertNull(archive.getId());
		for (PropertyDescriptor descriptor : descriptors) {
			if (descriptor.getName().equals("id") || descriptor.getName().equals("class")) {
				continue;
			}
			
			String getter = descriptor.getReadMethod().getName();
			assertEquals(invokeMethod(syncMessage, getter), invokeMethod(archive, getter));
		}
	}
	
}
