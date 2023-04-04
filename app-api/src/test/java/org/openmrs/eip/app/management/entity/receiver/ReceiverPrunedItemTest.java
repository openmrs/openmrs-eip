package org.openmrs.eip.app.management.entity.receiver;

import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

public class ReceiverPrunedItemTest {
	
	@Test
	public void shouldCreateAReceiverPrunedItemFromAnArchive() throws Exception {
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(SyncMessage.class);
		ReceiverSyncArchive archive = new ReceiverSyncArchive();
		archive.setId(1L);
		archive.setDateCreated(new Date());
		archive.setIdentifier("uuid");
		archive.setEntityPayload("payload");
		archive.setModelClassName(PersonModel.class.getName());
		archive.setSite(new SiteInfo());
		archive.setSnapshot(true);
		archive.setMessageUuid("message-uuid");
		archive.setDateSentBySender(LocalDateTime.now());
		archive.setOperation(SyncOperation.c);
		archive.setDateReceived(new Date());
		long timestamp = System.currentTimeMillis();
		
		ReceiverPrunedItem prune = new ReceiverPrunedItem(archive);
		
		Assert.assertNull(prune.getId());
		assertTrue(prune.getDateCreated().getTime() == timestamp || prune.getDateCreated().getTime() > timestamp);
		Set<String> ignored = new HashSet();
		ignored.add("id");
		ignored.add("class");
		ignored.add("dateCreated");
		for (PropertyDescriptor descriptor : descriptors) {
			if (ignored.contains(descriptor.getName())) {
				continue;
			}
			
			String getter = descriptor.getReadMethod().getName();
			assertEquals(invokeMethod(archive, getter), invokeMethod(prune, getter));
		}
	}
	
}
