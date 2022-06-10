package org.openmrs.eip.app.route.sender;

import org.openmrs.eip.app.route.BaseRouteTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@Import(SenderTestConfig.class)
@TestPropertySource(properties = "db-event.destinations=")
public abstract class BaseSenderRouteTest extends BaseRouteTest {
	
	@Override
	public String getAppFolderName() {
		return "sender";
	}
	
}
