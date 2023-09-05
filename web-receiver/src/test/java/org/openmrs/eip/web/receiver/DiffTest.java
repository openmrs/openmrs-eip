package org.openmrs.eip.web.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import org.junit.Test;
import org.openmrs.eip.component.model.PersonModel;

public class DiffTest {
	
	@Test
	public void createInstance_shouldCreateADiffBetweenTheSpecifiedStates() throws Exception {
		final String gender = "M";
		PersonModel currentState = new PersonModel();
		currentState.setGender(gender);
		currentState.setBirthdate(LocalDate.now());
		currentState.setBirthtime(LocalTime.now());
		currentState.setVoidReason("old reason");
		currentState.setDead(false);
		
		PersonModel newState = new PersonModel();
		newState.setGender(gender);
		newState.setBirthdate(null);
		newState.setBirthtime(null);
		newState.setVoidReason("new reason");
		newState.setDead(true);
		newState.setChangedByUuid("User(user-uuid)");
		newState.setDateChanged(LocalDateTime.now());
		
		Diff diff = Diff.createInstance(currentState, newState);
		
		Set<String> added = diff.getAdditions();
		assertEquals(2, added.size());
		assertTrue(added.contains("changedByUuid"));
		assertTrue(added.contains("dateChanged"));
		
		Set<String> removed = diff.getRemovals();
		assertEquals(2, removed.size());
		assertTrue(removed.contains("birthdate"));
		assertTrue(removed.contains("birthtime"));
		
		Set<String> modified = diff.getModifications();
		assertEquals(2, modified.size());
		assertTrue(modified.contains("voidReason"));
		assertTrue(modified.contains("dead"));
	}
	
}
