package org.openmrs.eip.app.route.receiver;

import org.openmrs.eip.app.route.BaseRouteTest;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(SyncProfiles.RECEIVER)
public abstract class BaseReceiverRouteTest extends BaseRouteTest {
	
	@Override
	public String getAppFolderName() {
		return "receiver";
	}
	
}
