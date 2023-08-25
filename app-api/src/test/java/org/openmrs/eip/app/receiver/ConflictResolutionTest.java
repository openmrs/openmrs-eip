package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.receiver.ConflictResolution.ResolutionDecision.IGNORE_NEW;
import static org.openmrs.eip.app.receiver.ConflictResolution.ResolutionDecision.MERGE;
import static org.openmrs.eip.app.receiver.ConflictResolution.ResolutionDecision.SYNC_NEW;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.component.exception.EIPException;

public class ConflictResolutionTest {
	
	@Test
	public void syncProperty_shouldFailIfDecisionIsIgnoreNew() {
		Throwable thrown = Assert.assertThrows(EIPException.class,
		    () -> new ConflictResolution(new ConflictQueueItem(), IGNORE_NEW).syncProperty("test"));
		assertEquals("Only merge resolution decision supports property level decisions", thrown.getMessage());
	}
	
	@Test
	public void syncProperty_shouldFailIfDecisionIsSyncNew() {
		Throwable thrown = Assert.assertThrows(EIPException.class,
		    () -> new ConflictResolution(new ConflictQueueItem(), SYNC_NEW).syncProperty("test"));
		assertEquals("Only merge resolution decision supports property level decisions", thrown.getMessage());
	}
	
	@Test
	public void syncProperty_shouldAddThePropertyToTheListOfSyncedProperties() {
		final String PROPERTY_NAME = "test";
		ConflictResolution resolution = new ConflictResolution(new ConflictQueueItem(), MERGE);
		assertFalse(resolution.isSynced(PROPERTY_NAME));
		
		resolution.syncProperty(PROPERTY_NAME);
		
		assertTrue(resolution.isSynced(PROPERTY_NAME));
	}
	
	@Test
	public void isSynced_shouldReturnTrueForAnSyncedProperty() {
		final String PROPERTY_NAME = "test";
		ConflictResolution resolution = new ConflictResolution(new ConflictQueueItem(), MERGE);
		assertTrue(resolution.getSyncedProperties().add(PROPERTY_NAME));
		assertTrue(resolution.isSynced(PROPERTY_NAME));
	}
	
	@Test
	public void isSynced_shouldReturnFalseForANoneSyncedProperty() {
		ConflictResolution resolution = new ConflictResolution(new ConflictQueueItem(), MERGE);
		assertTrue(resolution.getSyncedProperties().add("test"));
		assertFalse(resolution.isSynced("other"));
	}
	
}
