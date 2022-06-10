package org.openmrs.eip.app.route;

import org.junit.Before;
import org.openmrs.eip.BaseDbBackedCamelTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "logging.level.org.apache.camel.reifier.RouteReifier=WARN")
public abstract class BaseRouteTest extends BaseDbBackedCamelTest {
	
	public abstract String getTestRouteFilename();
	
	public abstract String getAppFolderName();
	
	@Before
	public void setupBaseRouteTest() throws Exception {
		loadRoute(getTestRouteFilename() + ".xml");
	}
	
	protected void loadRoute(String routeFilename) throws Exception {
		loadXmlRoutes(getAppFolderName(), routeFilename);
	}
	
}
