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
import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.exception.EIPException;
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
	
	@Test
	public void shouldCreateAReceiverArchiveFromARetryQueueItem() throws Exception {
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(ReceiverRetryQueueItem.class);
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setId(1L);
		retry.setDateCreated(new Date());
		retry.setIdentifier("uuid");
		retry.setEntityPayload("payload");
		retry.setModelClassName(PersonModel.class.getName());
		retry.setSite(new SiteInfo());
		retry.setSnapshot(true);
		retry.setMessageUuid("message-uuid");
		retry.setDateSentBySender(LocalDateTime.now());
		retry.setExceptionType(EIPException.class.getName());
		retry.setMessage(EIPException.class.getName());
		retry.setAttemptCount(1);
		retry.setDateChanged(new Date());
		retry.setDateReceived(new Date());
		
		ReceiverSyncArchive archive = new ReceiverSyncArchive(retry);
		
		Assert.assertNull(archive.getId());
		Assert.assertNull(archive.getDateCreated());
		Set<String> ignored = new HashSet();
		ignored.add("id");
		ignored.add("class");
		ignored.add("dateCreated");
		ignored.add("exceptionType");
		ignored.add("message");
		ignored.add("attemptCount");
		ignored.add("dateChanged");
		for (PropertyDescriptor descriptor : descriptors) {
			if (ignored.contains(descriptor.getName())) {
				continue;
			}
			
			String getter = descriptor.getReadMethod().getName();
			assertEquals(invokeMethod(retry, getter), invokeMethod(archive, getter));
		}
	}
	
	@Test
	public void shouldCreateAReceiverArchiveFromAConflictQueueItem() throws Exception {
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(ConflictQueueItem.class);
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setId(1L);
		conflict.setDateCreated(new Date());
		conflict.setIdentifier("uuid");
		conflict.setEntityPayload("payload");
		conflict.setModelClassName(PersonModel.class.getName());
		conflict.setSite(new SiteInfo());
		conflict.setSnapshot(true);
		conflict.setMessageUuid("message-uuid");
		conflict.setDateSentBySender(LocalDateTime.now());
		conflict.setDateReceived(new Date());
		conflict.setOperation(SyncOperation.u);
		conflict.setResolved(true);
		
		ReceiverSyncArchive archive = new ReceiverSyncArchive(conflict);
		
		Assert.assertNull(archive.getId());
		Assert.assertNull(archive.getDateCreated());
		Set<String> ignored = new HashSet();
		ignored.add("id");
		ignored.add("class");
		ignored.add("dateCreated");
		ignored.add("resolved");
		for (PropertyDescriptor descriptor : descriptors) {
			if (ignored.contains(descriptor.getName())) {
				continue;
			}
			
			String getter = descriptor.getReadMethod().getName();
			assertEquals(invokeMethod(conflict, getter), invokeMethod(archive, getter));
		}
	}
	
}
