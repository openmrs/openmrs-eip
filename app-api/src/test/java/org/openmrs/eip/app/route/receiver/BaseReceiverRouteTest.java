package org.openmrs.eip.app.route.receiver;

import org.openmrs.eip.app.receiver.ReceiverConstants;
import org.openmrs.eip.app.route.BaseRouteTest;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles(SyncProfiles.RECEIVER)
@TestPropertySource(properties = ReceiverConstants.PROP_CAMEL_OUTPUT_ENDPOINT + "=")
public abstract class BaseReceiverRouteTest extends BaseRouteTest {
	
	@Override
	public String getAppFolderName() {
		return "receiver";
	}
	
}
