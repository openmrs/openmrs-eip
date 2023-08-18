package org.openmrs.eip.app.management.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.receiver.ConflictResolution.ResolutionDecision.IGNORE_NEW;
import static org.openmrs.eip.app.receiver.ConflictResolution.ResolutionDecision.MERGE;

import javax.xml.ws.Holder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.ConflictResolution;
import org.openmrs.eip.app.receiver.ConflictResolution.ResolutionDecision;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.PersonModel;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ConflictServiceImplTest {
	
	private ConflictServiceImpl service;
	
	@Mock
	private ReceiverService mockReceiverService;
	
	@Before
	public void setup() {
		service = new ConflictServiceImpl(null, null, null, mockReceiverService, null, null);
	}
	
	@Test
	public void resolve_shouldFailIfConflictIsNull() {
		Throwable thrown = Assert.assertThrows(EIPException.class,
		    () -> service.resolve(new ConflictResolution(null, IGNORE_NEW)));
		assertEquals("Conflict is required", thrown.getMessage());
	}
	
	@Test
	public void resolve_shouldFailIfResolutionIsNull() {
		Throwable thrown = Assert.assertThrows(EIPException.class,
		    () -> service.resolve(new ConflictResolution(new ConflictQueueItem(), null)));
		assertEquals("Resolution is required", thrown.getMessage());
	}
	
	@Test
	public void resolve_shouldMoveTheItemToTheArchivesIfDecisionIsSetToIgnoreNew() {
		service = Mockito.spy(service);
		ConflictQueueItem conflict = new ConflictQueueItem();
		Holder<Boolean> holder = new Holder();
		Mockito.doAnswer(invocation -> {
			holder.value = true;
			return null;
		}).when(service).moveToArchiveQueue(conflict);
		
		service.resolve(new ConflictResolution(conflict, IGNORE_NEW));
		
		assertTrue(holder.value);
	}
	
	@Test
	public void resolve_shouldUpdateTheHashAndMoveTheItemToTheRetryQueueIfDecisionIsSetToSyncNew() {
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
		
		service.resolve(new ConflictResolution(conflict, ResolutionDecision.SYNC_NEW));
		
		Mockito.verify(mockReceiverService).updateHash(modelClassName, uuid);
		assertTrue(holder.value);
	}
	
	@Test
	public void resolve_shouldFailForAMergeResolutionAndNoSyncedPropertiesSpecified() {
		Throwable thrown = Assert.assertThrows(EIPException.class,
		    () -> service.resolve(new ConflictResolution(new ConflictQueueItem(), MERGE)));
		assertEquals("No properties to sync specified for merge resolution decision", thrown.getMessage());
	}
	
}
