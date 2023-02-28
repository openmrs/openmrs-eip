package org.openmrs.eip.app.route.sender;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.eip.app.route.BaseRouteValidatorTest;

public class SenderRouteValidatorTest extends BaseRouteValidatorTest {
	
	private static final Set<String> retryErrorHandlerRoutes;
	
	static {
		retryErrorHandlerRoutes = new HashSet();
		retryErrorHandlerRoutes.add("debezium-event-processor");
		retryErrorHandlerRoutes.add("db-event-processor");
		retryErrorHandlerRoutes.add("out-bound-db-sync");
		retryErrorHandlerRoutes.add("sender-retry");
	}
	
	@Override
	public String getAppFolder() {
		return "sender";
	}
	
	@Override
	public String getRetryHandlerRef() {
		return "outBoundErrorHandler";
	}
	
	@Override
	public Set<String> getRoutesWithRetryHandler() {
		return retryErrorHandlerRoutes;
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
