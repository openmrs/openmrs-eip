package org.openmrs.eip.app;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AppUtilsTest {
	
	@Test
	public void shouldReturnTrueForASubclassTable() {
		assertTrue(AppUtils.isSubclassTable("patient"));
		assertTrue(AppUtils.isSubclassTable("PATIENT"));
		assertTrue(AppUtils.isSubclassTable("test_order"));
		assertTrue(AppUtils.isSubclassTable("TEST_ORDER"));
		assertTrue(AppUtils.isSubclassTable("drug_order"));
		assertTrue(AppUtils.isSubclassTable("DRUG_ORDER"));
	}
	
	@Test
	public void shouldReturnFalseForANonSubclassTable() {
		assertFalse(AppUtils.isSubclassTable("person"));
		assertFalse(AppUtils.isSubclassTable("orders"));
	}
	
}
