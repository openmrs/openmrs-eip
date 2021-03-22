package org.openmrs.eip.component.entity;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

public class BaseChangeableMetaDataEntityTest {

    @Test
    public void wasModifiedAfter_shouldReturnFalseIfTheOtherEntityHasTheEarliestDateChangedOrRetired() {
        OrderFrequency frequency = new OrderFrequency();
        frequency.setDateRetired(LocalDateTime.of(2020, 12, 3, 12, 12, 12));
        OrderFrequency other = new OrderFrequency();
        other.setDateRetired(LocalDateTime.of(2020, 12, 3, 12, 12, 13));
        Assert.assertFalse(frequency.wasModifiedAfter(other));

        other.setDateRetired(LocalDateTime.of(2020, 12, 3, 12, 12, 11));
        other.setDateChanged(LocalDateTime.of(2020, 12, 3, 12, 12, 13));
        Assert.assertFalse(frequency.wasModifiedAfter(other));
    }

    @Test
    public void wasModifiedAfter_shouldReturnTrueIfTheEntityHasTheEarliestDateChangedOrRetired() {
        OrderFrequency frequency = new OrderFrequency();
        frequency.setDateRetired(LocalDateTime.of(2020, 12, 3, 12, 12, 13));
        OrderFrequency other = new OrderFrequency();
        other.setDateRetired(LocalDateTime.of(2020, 12, 3, 12, 12, 12));
        Assert.assertTrue(frequency.wasModifiedAfter(other));

        frequency.setDateRetired(LocalDateTime.of(2020, 12, 3, 12, 12, 11));
        frequency.setDateChanged(LocalDateTime.of(2020, 12, 3, 12, 12, 13));
        Assert.assertTrue(frequency.wasModifiedAfter(other));
    }
}
