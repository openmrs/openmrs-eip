package org.openmrs.eip.app.management.entity.sender;

import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SenderSyncMessage;
import org.springframework.beans.BeanUtils;

public class SenderSyncArchiveTest {
	
	@Test
	public void shouldCreateASenderArchiveFromASyncMessage() throws Exception {
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(SenderSyncMessage.class);
		SenderSyncMessage syncMessage = new SenderSyncMessage();
		syncMessage.setId(1L);
		syncMessage.setDateCreated(new Date());
		syncMessage.setIdentifier("uuid");
		syncMessage.setTableName("person");
		syncMessage.setOperation("c");
		syncMessage.setEventDate(new Date());
		syncMessage.setSnapshot(true);
		syncMessage.setMessageUuid("message-uuid");
		syncMessage.setRequestUuid("request-uuid");
		syncMessage.markAsSent(LocalDateTime.now());
		syncMessage.setData("{}");
		
		SenderSyncArchive archive = new SenderSyncArchive(syncMessage);
		
		Assert.assertNull(archive.getId());
		Assert.assertNull(archive.getDateCreated());
		Set<String> ignored = new HashSet();
		ignored.add("id");
		ignored.add("class");
		ignored.add("dateCreated");
		ignored.add("status");
		for (PropertyDescriptor descriptor : descriptors) {
			if (ignored.contains(descriptor.getName())) {
				continue;
			}
			
			String getter = descriptor.getReadMethod().getName();
			assertEquals(invokeMethod(syncMessage, getter), invokeMethod(archive, getter));
		}
	}
	
}
