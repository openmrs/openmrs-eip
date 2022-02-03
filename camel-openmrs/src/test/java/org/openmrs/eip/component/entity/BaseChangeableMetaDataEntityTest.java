package org.openmrs.eip.component.entity;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;

public class BaseChangeableMetaDataEntityTest {
	
	@Test
	public void wasModifiedAfter_shouldReturnFalseIfTheOtherEntityHasTheEarliestDateChangedOrRetired() {
		Location location = new Location();
		location.setDateRetired(LocalDateTime.of(2020, 12, 3, 12, 12, 12));
		Location other = new Location();
		other.setDateRetired(LocalDateTime.of(2020, 12, 3, 12, 12, 13));
		Assert.assertFalse(location.wasModifiedAfter(other));
		
		other.setDateRetired(LocalDateTime.of(2020, 12, 3, 12, 12, 11));
		other.setDateChanged(LocalDateTime.of(2020, 12, 3, 12, 12, 13));
		Assert.assertFalse(location.wasModifiedAfter(other));
	}
	
	@Test
	public void wasModifiedAfter_shouldReturnTrueIfTheEntityHasTheEarliestDateChangedOrRetired() {
		Location location = new Location();
		location.setDateRetired(LocalDateTime.of(2020, 12, 3, 12, 12, 13));
		Location other = new Location();
		other.setDateRetired(LocalDateTime.of(2020, 12, 3, 12, 12, 12));
		Assert.assertTrue(location.wasModifiedAfter(other));
		
		location.setDateRetired(LocalDateTime.of(2020, 12, 3, 12, 12, 11));
		location.setDateChanged(LocalDateTime.of(2020, 12, 3, 12, 12, 13));
		Assert.assertTrue(location.wasModifiedAfter(other));
	}
}
