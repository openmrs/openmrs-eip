package org.openmrs.eip.app.management.service.impl;

import javax.xml.ws.Holder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ConflictServiceImplTest {
	
	private ConflictServiceImpl service;
	
	@Before
	public void setup() {
		service = new ConflictServiceImpl(null, null, null, null);
	}
	
	@Test
	public void resolveWithDatabaseState_shouldMoveTheItemToTheArchives() {
		service = Mockito.spy(service);
		ConflictQueueItem conflict = new ConflictQueueItem();
		Holder<Boolean> archived = new Holder();
		Mockito.doAnswer(invocation -> {
			archived.value = true;
			return null;
		}).when(service).moveToArchiveQueue(conflict);
		
		service.resolveWithDatabaseState(conflict);
		
		Assert.assertTrue(archived.value);
	}
	
}
