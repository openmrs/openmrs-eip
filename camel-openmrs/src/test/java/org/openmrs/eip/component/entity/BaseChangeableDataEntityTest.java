package org.openmrs.eip.component.entity;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

public class BaseChangeableDataEntityTest {
	
	@Test
	public void wasModifiedAfter_shouldReturnFalseIfTheOtherEntityHasTheEarliestDateChangedOrVoided() {
		Condition condition = new Condition();
		condition.setDateVoided(LocalDateTime.of(2020, 12, 3, 12, 12, 12));
		Condition other = new Condition();
		other.setDateVoided(LocalDateTime.of(2020, 12, 3, 12, 12, 13));
		Assert.assertFalse(condition.wasModifiedAfter(other));
		
		other.setDateVoided(LocalDateTime.of(2020, 12, 3, 12, 12, 11));
		other.setDateChanged(LocalDateTime.of(2020, 12, 3, 12, 12, 13));
		Assert.assertFalse(condition.wasModifiedAfter(other));
	}
	
	@Test
	public void wasModifiedAfter_shouldReturnTrueIfTheEntityHasTheEarliestDateChangedOrVoided() {
		Condition condition = new Condition();
		condition.setDateVoided(LocalDateTime.of(2020, 12, 3, 12, 12, 13));
		Condition other = new Condition();
		other.setDateVoided(LocalDateTime.of(2020, 12, 3, 12, 12, 12));
		Assert.assertTrue(condition.wasModifiedAfter(other));
		
		condition.setDateVoided(LocalDateTime.of(2020, 12, 3, 12, 12, 11));
		condition.setDateChanged(LocalDateTime.of(2020, 12, 3, 12, 12, 13));
		Assert.assertTrue(condition.wasModifiedAfter(other));
	}
	
}
