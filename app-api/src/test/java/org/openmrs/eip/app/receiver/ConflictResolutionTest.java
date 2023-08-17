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
	public void addPropertyDecision_shouldFailIfDecisionIsIgnoreNew() {
		Throwable thrown = Assert.assertThrows(EIPException.class,
		    () -> new ConflictResolution(new ConflictQueueItem(), IGNORE_NEW).ignoreProperty("test"));
		assertEquals("Only merge resolution decision supports property level decisions", thrown.getMessage());
	}
	
	@Test
	public void addPropertyDecision_shouldFailIfDecisionIsSyncNew() {
		Throwable thrown = Assert.assertThrows(EIPException.class,
		    () -> new ConflictResolution(new ConflictQueueItem(), SYNC_NEW).ignoreProperty("test"));
		assertEquals("Only merge resolution decision supports property level decisions", thrown.getMessage());
	}
	
	@Test
	public void isIgnored_shouldReturnTrueForAnIgnoredProperty() {
		final String PROPERTY_NAME = "test";
		ConflictResolution resolution = new ConflictResolution(new ConflictQueueItem(), MERGE);
		assertTrue(resolution.getIgnoredProperties().add(PROPERTY_NAME));
		assertTrue(resolution.isIgnored(PROPERTY_NAME));
	}
	
	@Test
	public void isIgnored_shouldReturnFalseForANoneIgnoredProperty() {
		ConflictResolution resolution = new ConflictResolution(new ConflictQueueItem(), MERGE);
		assertTrue(resolution.getIgnoredProperties().add("test"));
		assertFalse(resolution.isIgnored("other"));
	}
	
	@Test
	public void ignoreProperty_shouldAddThePropertyToTheListOfIgnoredProperties() {
		final String PROPERTY_NAME = "test";
		ConflictResolution resolution = new ConflictResolution(new ConflictQueueItem(), MERGE);
		assertFalse(resolution.isIgnored(PROPERTY_NAME));
		
		resolution.ignoreProperty(PROPERTY_NAME);
		
		assertTrue(resolution.isIgnored(PROPERTY_NAME));
	}
	
}
