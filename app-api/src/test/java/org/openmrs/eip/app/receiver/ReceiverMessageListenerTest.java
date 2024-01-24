package org.openmrs.eip.app.receiver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.eip.app.AppUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppUtils.class)
public class ReceiverMessageListenerTest {
	
	@Test
	public void onMessage_shouldShutdownTheApplicationWhenAnErrorIsEncountered() {
		PowerMockito.mockStatic(AppUtils.class);
		new ReceiverMessageListener(null, null).onMessage(null);
		PowerMockito.verifyStatic(AppUtils.class);
		AppUtils.shutdown();
	}
	
}
