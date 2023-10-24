package org.openmrs.eip.app.sender;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BinlogUtils.class)
public class BinlogPurgingTaskTest {
	
	@Mock
	private File mockFile;
	
	@Test
	public void run_shouldPurgeProcessedLogFiles() throws Exception {
		PowerMockito.mockStatic(BinlogUtils.class);
		final int maxKeepCount = 5;
		BinlogPurgingTask task = new BinlogPurgingTask(mockFile, maxKeepCount);
		
		Assert.assertTrue(task.doRun());
		
		PowerMockito.verifyStatic(BinlogUtils.class);
		BinlogUtils.purgeBinLogs(mockFile, maxKeepCount);
	}
	
}
