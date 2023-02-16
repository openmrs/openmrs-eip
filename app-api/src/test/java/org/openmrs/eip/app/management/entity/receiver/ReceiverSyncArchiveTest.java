package org.openmrs.eip.app.management.entity.receiver;

import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.PersonModel;
import org.springframework.beans.BeanUtils;

public class ReceiverSyncArchiveTest {
	
	@Test
	public void shouldCreateAReceiverArchiveFromAProcessedMessage() throws Exception {
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(SyncMessage.class);
		SyncedMessage msg = new SyncedMessage();
		msg.setId(1L);
		msg.setDateCreated(new Date());
		msg.setIdentifier("uuid");
		msg.setEntityPayload("payload");
		msg.setModelClassName(PersonModel.class.getName());
		msg.setSite(new SiteInfo());
		msg.setSnapshot(true);
		msg.setMessageUuid("message-uuid");
		msg.setDateSentBySender(LocalDateTime.now());
		msg.setOperation(SyncOperation.c);
		
		ReceiverSyncArchive archive = new ReceiverSyncArchive(msg);
		
		Assert.assertNull(archive.getId());
		Assert.assertNull(archive.getDateCreated());
		Assert.assertEquals(msg.getDateCreated(), archive.getDateReceived());
		Set<String> ignored = new HashSet();
		ignored.add("id");
		ignored.add("class");
		ignored.add("dateCreated");
		for (PropertyDescriptor descriptor : descriptors) {
			if (ignored.contains(descriptor.getName())) {
				continue;
			}
			
			String getter = descriptor.getReadMethod().getName();
			assertEquals(invokeMethod(msg, getter), invokeMethod(archive, getter));
		}
	}
	
}
