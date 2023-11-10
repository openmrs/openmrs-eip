package org.openmrs.eip.app.management.entity.sender;

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
import org.openmrs.eip.component.SyncOperation;
import org.springframework.beans.BeanUtils;

public class SenderPrunedArchiveTest {
	
	@Test
	public void shouldCreateAPrunedArchiveFromASyncArchive() throws Exception {
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(SenderSyncArchive.class);
		SenderSyncArchive archive = new SenderSyncArchive();
		archive.setId(1L);
		archive.setDateCreated(new Date());
		archive.setIdentifier("uuid");
		archive.setData("data");
		archive.setTableName("person");
		archive.setSnapshot(true);
		archive.setMessageUuid("message-uuid");
		archive.setRequestUuid("req-uuid");
		archive.setEventDate(new Date());
		archive.setDateSent(new Date());
		archive.setDateReceivedByReceiver(LocalDateTime.now());
		archive.setOperation(SyncOperation.c.name());
		long timestamp = System.currentTimeMillis();
		
		SenderPrunedArchive prune = new SenderPrunedArchive(archive);
		
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
