package org.openmrs.eip.web.receiver;

import org.junit.Assert;
import org.junit.Test;

public class ReceiverDashboardHelperTest {
	
	@Test
	public void getCategorizationProperty_shouldReturnModelClassName() {
		ReceiverDashboardHelper helper = new ReceiverDashboardHelper(null);
		Assert.assertEquals("modelClassName", helper.getCategorizationProperty(null));
	}
	
}
