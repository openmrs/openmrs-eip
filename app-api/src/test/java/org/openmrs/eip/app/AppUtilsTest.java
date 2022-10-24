package org.openmrs.eip.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class AppUtilsTest {
	
	private static final String TEST_VERSION = "1.0 Caramel";
	
	private static final String TEST_BUILD_NO = "123456";
	
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
	
	@Test
	public void getVersion_shouldReturnTheVersion() {
		Properties props = new Properties();
		props.setProperty(SyncConstants.DBSYNC_PROP_VERSION, TEST_VERSION);
		Whitebox.setInternalState(AppUtils.class, "PROPERTIES", props);
		assertEquals(TEST_VERSION, AppUtils.getVersion());
	}
	
	@Test
	public void getBuild_shouldReturnTheBuildNumber() {
		Properties props = new Properties();
		props.setProperty(SyncConstants.DBSYNC_PROP_BUILD_NUMBER, TEST_BUILD_NO);
		Whitebox.setInternalState(AppUtils.class, "PROPERTIES", props);
		assertEquals(TEST_BUILD_NO, AppUtils.getBuildNumber());
	}
	
}
