package org.openmrs.eip.app.receiver;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.component.exception.EIPException;

public class ReceiverUtilsTest {
	
	@Test
	public void process_shouldReturnTheErrorMessage() {
		EIPException e = new EIPException("Testing");
		
		Assert.assertEquals(e.toString(), ReceiverUtils.getErrorMessage(e));
	}
	
	@Test
	public void process_shouldReturnTheRootCauseErrorMessage() {
		final String rootCauseMsg = "test root error";
		Exception root = new ActiveMQException(rootCauseMsg);
		Exception e = new EIPException("test1", new Exception("test2", root));
		
		Assert.assertEquals(root.toString(), ReceiverUtils.getErrorMessage(e));
	}
	
	@Test
	public void process_shouldTruncateTheErrorMessageIfLongerThan1024() {
		final String errMsg = RandomStringUtils.randomAscii(1025);
		EIPException e = new EIPException(errMsg);
		
		Assert.assertEquals(e.toString().substring(0, 1024), ReceiverUtils.getErrorMessage(e));
	}
	
}
