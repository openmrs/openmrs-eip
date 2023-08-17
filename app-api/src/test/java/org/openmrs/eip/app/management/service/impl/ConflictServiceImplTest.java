package org.openmrs.eip.app.management.service.impl;

import static org.junit.Assert.assertTrue;

import javax.xml.ws.Holder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.component.model.PersonModel;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ConflictServiceImplTest {
	
	private ConflictServiceImpl service;
	
	@Mock
	private ReceiverService mockReceiverService;
	
	@Before
	public void setup() {
		service = new ConflictServiceImpl(null, null, null, mockReceiverService);
	}
	
	@Test
	public void resolveWithDatabaseState_shouldMoveTheItemToTheArchives() {
		service = Mockito.spy(service);
		ConflictQueueItem conflict = new ConflictQueueItem();
		Holder<Boolean> holder = new Holder();
		Mockito.doAnswer(invocation -> {
			holder.value = true;
			return null;
		}).when(service).moveToArchiveQueue(conflict);
		
		service.resolveWithDatabaseState(conflict);
		
		assertTrue(holder.value);
	}
	
	@Test
	public void resolveWithNewState_shouldUpdateTheHashAndMoveTheItemToTheRetryQueue() {
		final String modelClassName = PersonModel.class.getName();
		final String uuid = "person-uuid";
		service = Mockito.spy(service);
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setModelClassName(modelClassName);
		conflict.setIdentifier(uuid);
		Holder<Boolean> holder = new Holder();
		Mockito.doAnswer(invocation -> {
			holder.value = true;
			return null;
		}).when(service).moveToRetryQueue(conflict, "Moved from conflict queue after conflict resolution");
		
		service.resolveWithNewState(conflict);
		
		Mockito.verify(mockReceiverService).updateHash(modelClassName, uuid);
		assertTrue(holder.value);
	}
	
}
