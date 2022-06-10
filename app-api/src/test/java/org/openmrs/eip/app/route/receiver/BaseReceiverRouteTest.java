package org.openmrs.eip.app.route.receiver;

import org.openmrs.eip.app.route.BaseRouteTest;
import org.springframework.context.annotation.Import;

@Import(ReceiverTestConfig.class)
public abstract class BaseReceiverRouteTest extends BaseRouteTest {
	
	@Override
	public String getAppFolderName() {
		return "receiver";
	}
	
}
