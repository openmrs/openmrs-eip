package org.openmrs.eip.app.sender;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.SyncModel;

public class SenderUtilsTest {
	
	@Test
	public void mask_shouldFailForAValueOfATypeThatIsNotSupported() {
		SyncModel model = new SyncModel();
		Exception thrown = Assert.assertThrows(EIPException.class, () -> SenderUtils.mask(model));
		Assert.assertEquals("Don't know how mask a value of type: " + model.getClass(), thrown.getMessage());
	}
	
	@Test
	public void mask_shouldReturnNullForANullValue() {
		Assert.assertNull(SenderUtils.mask(null));
	}
	
	@Test
	public void mask_shouldReturnTheCorrectMaskValueForAString() {
		Assert.assertEquals(SenderConstants.MASK, SenderUtils.mask("test"));
	}
	
}
