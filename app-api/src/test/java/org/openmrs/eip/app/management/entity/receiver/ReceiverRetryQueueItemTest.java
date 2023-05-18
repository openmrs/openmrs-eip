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
import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.exception.ConflictsFoundException;
import org.openmrs.eip.component.model.PersonModel;
import org.springframework.beans.BeanUtils;

public class ReceiverRetryQueueItemTest {
	
	@Test
	public void shouldCreateARetryItemFromAConflict() throws Exception {
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
		conflict.setOperation(SyncOperation.u);
		conflict.setDateReceived(new Date());
		conflict.setResolved(true);
		long timestamp = System.currentTimeMillis();
		
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem(conflict);
		
		Assert.assertNull(retry.getId());
		assertTrue(retry.getDateCreated().getTime() == timestamp || retry.getDateCreated().getTime() > timestamp);
		assertEquals(ConflictsFoundException.class.getName(), retry.getExceptionType());
		assertEquals("", retry.getMessage());
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
			assertEquals(invokeMethod(conflict, getter), invokeMethod(retry, getter));
		}
	}
	
}
