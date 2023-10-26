package org.openmrs.eip.web.receiver;

import org.junit.Assert;
import org.junit.Test;

public class ReceiverDashboardGeneratorTest {
	
	@Test
	public void getCategorizationProperty_shouldReturnModelClassName() {
		ReceiverDashboardGenerator generator = new ReceiverDashboardGenerator(null);
		Assert.assertEquals("modelClassName", generator.getCategorizationProperty(null));
	}
	
}
