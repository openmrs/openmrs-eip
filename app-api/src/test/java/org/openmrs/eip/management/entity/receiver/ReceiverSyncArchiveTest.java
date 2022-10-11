package org.openmrs.eip.management.entity.receiver;

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
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.PersonModel;
import org.springframework.beans.BeanUtils;

public class ReceiverSyncArchiveTest {
	
	@Test
	public void shouldCreateAReceiverArchiveFromASyncMessage() throws Exception {
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(SyncMessage.class);
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
		Assert.assertNull(archive.getDateCreated());
		Assert.assertEquals(syncMessage.getDateCreated(), archive.getDateReceived());
		Set<String> ignored = new HashSet();
		ignored.add("id");
		ignored.add("class");
		ignored.add("dateCreated");
		for (PropertyDescriptor descriptor : descriptors) {
			if (ignored.contains(descriptor.getName())) {
				continue;
			}
			
			String getter = descriptor.getReadMethod().getName();
			assertEquals(invokeMethod(syncMessage, getter), invokeMethod(archive, getter));
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
		ConflictQueueItem retry = new ConflictQueueItem();
		retry.setId(1L);
		retry.setDateCreated(new Date());
		retry.setIdentifier("uuid");
		retry.setEntityPayload("payload");
		retry.setModelClassName(PersonModel.class.getName());
		retry.setSite(new SiteInfo());
		retry.setSnapshot(true);
		retry.setMessageUuid("message-uuid");
		retry.setDateSentBySender(LocalDateTime.now());
		retry.setDateReceived(new Date());
		
		ReceiverSyncArchive archive = new ReceiverSyncArchive(retry);
		
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
			assertEquals(invokeMethod(retry, getter), invokeMethod(archive, getter));
		}
	}
	
}
