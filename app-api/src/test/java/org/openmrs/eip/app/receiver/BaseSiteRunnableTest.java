package org.openmrs.eip.app.receiver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.MockBaseSiteRunnable;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppUtils.class)
public class BaseSiteRunnableTest {
	
	@Mock
	private SiteInfo mockSite;
	
	private MockBaseSiteRunnable runnable;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(AppUtils.class);
		runnable = new MockBaseSiteRunnable(mockSite);
	}
	
	@Test
	public void run_shouldInvokeDoRun() {
		runnable.run();
		Assert.assertTrue(runnable.isDoRunCalled());
	}
	
	@Test
	public void run_shouldSkipRunningIfTheApplicationIsStopping() {
		Mockito.when(AppUtils.isStopping()).thenReturn(true);
		
		runnable.run();
		
		Assert.assertFalse(runnable.isDoRunCalled());
	}
	
}
