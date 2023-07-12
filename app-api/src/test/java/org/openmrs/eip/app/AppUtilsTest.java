package org.openmrs.eip.app;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.app.receiver.ReceiverConstants.DEFAULT_TASK_BATCH_SIZE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_SYNC_TASK_BATCH_SIZE;

import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SyncContext.class)
public class AppUtilsTest {
	
	private static final String TEST_VERSION = "1.0 Caramel";
	
	private static final String TEST_BUILD_NO = "123456";
	
	@Mock
	private Environment mockEnv;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		when(SyncContext.getBean(Environment.class)).thenReturn(mockEnv);
	}
	
	@After
	public void tearDown() {
		Whitebox.setInternalState(AppUtils.class, "props", (Object) null);
	}
	
	@Test
	public void getVersion_shouldReturnTheVersion() {
		Properties props = new Properties();
		props.setProperty(SyncConstants.DBSYNC_PROP_VERSION, TEST_VERSION);
		Whitebox.setInternalState(AppUtils.class, "props", props);
		assertEquals(TEST_VERSION, AppUtils.getVersion());
	}
	
	@Test
	public void getBuild_shouldReturnTheBuildNumber() {
		Properties props = new Properties();
		props.setProperty(SyncConstants.DBSYNC_PROP_BUILD_NUMBER, TEST_BUILD_NO);
		Whitebox.setInternalState(AppUtils.class, "props", props);
		assertEquals(TEST_BUILD_NO, AppUtils.getBuildNumber());
	}
	
	@Test
	public void getTaskPage_shouldGetThePageableObjectUsingConfiguredBatchSize() {
		Assert.assertNull(Whitebox.getInternalState(AppUtils.class, "taskPage"));
		final int size = 5;
		when(mockEnv.getProperty(PROP_SYNC_TASK_BATCH_SIZE, Integer.class, DEFAULT_TASK_BATCH_SIZE)).thenReturn(size);
		
		Pageable pageable = AppUtils.getTaskPage();
		
		assertEquals(0, pageable.getPageNumber());
		assertEquals(size, pageable.getPageSize());
	}
	
}
