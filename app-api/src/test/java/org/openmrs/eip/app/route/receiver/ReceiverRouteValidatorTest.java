package org.openmrs.eip.app.route.receiver;

import java.util.Collections;
import java.util.Set;

import org.openmrs.eip.app.route.BaseRouteValidatorTest;

public class ReceiverRouteValidatorTest extends BaseRouteValidatorTest {
	
	@Override
	public String getAppFolder() {
		return "receiver";
	}
	
	@Override
	public String getRetryHandlerRef() {
		return null;
	}
	
	@Override
	public Set<String> getRoutesWithRetryHandler() {
		return Collections.emptySet();
	}
	
	@Override
	public Set<String> getRoutesWithDeadLetterChannelHandler() {
		return Collections.emptySet();
	}
	
	@Override
	public Set<String> getRoutesWithNoErrorHandler() {
		return Collections.emptySet();
	}
	
}
