package org.openmrs.eip.app.route;

import static java.io.File.separator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.FOLDER_DIST;
import static org.openmrs.eip.app.SyncConstants.FOLDER_ROUTES;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.Document;

public abstract class BaseRouteValidatorTest {
	
	private static XPath xpath = XPathFactory.newInstance().newXPath();
	
	private static DocumentBuilder documentBuilder;
	
	private static XPathExpression routeIdExpression;
	
	private static XPathExpression errorHandlerExpression;
	
	private static final List<String> noErrorHandlerDefinedRoutes = Arrays.asList("dlc-route", "shutdown-route");
	
	public abstract String getAppFolder();
	
	public abstract String getRetryHandlerRef();
	
	public abstract Set<String> getRoutesWithRetryHandler();
	
	public abstract Set<String> getRoutesWithDeadLetterChannelHandler();
	
	public abstract Set<String> getRoutesWithNoErrorHandler();
	
	@BeforeClass
	public static void setupBaseRouteValidatorTest() throws Exception {
		documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		routeIdExpression = xpath.compile("/routes/route/@id");
		errorHandlerExpression = xpath.compile("/routes/route/@errorHandlerRef");
	}
	
	@Test
	public void validateRoutes() throws Exception {
		FileSystemResourceLoader routesLoader = new FileSystemResourceLoader();
		PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver(routesLoader);
		String patternPrefix = ".." + separator + FOLDER_DIST + separator + getAppFolder() + separator + FOLDER_ROUTES;
		if (!SystemUtils.getUserDir().getName().equals("app-api")) {
			patternPrefix = FOLDER_DIST + separator + getAppFolder() + separator + FOLDER_ROUTES;
		}
		
		Resource[] resources = resourceResolver.getResources(patternPrefix + separator + "*.xml");
		if (resources.length == 0) {
			throw new Exception("No routes found to validate");
		}
		
		for (Resource resource : resources) {
			validateRoute(resource);
		}
	}
	
	private void validateRoute(Resource resource) throws Exception {
		Document document = documentBuilder.parse(resource.getInputStream());
		String routeId = routeIdExpression.evaluate(document);
		String errorHandlerRef = errorHandlerExpression.evaluate(document);
		final String msg = "Invalid error handler ref defined for route with id: " + routeId;
		if (noErrorHandlerDefinedRoutes.contains(routeId)) {
			Assert.assertTrue(StringUtils.isBlank(errorHandlerRef));
		} else if (getRoutesWithRetryHandler().contains(routeId)) {
			assertEquals(msg, getRetryHandlerRef(), errorHandlerRef);
		} else if (getRoutesWithDeadLetterChannelHandler().contains(routeId)) {
			assertEquals(msg, "deadLetterChannelBuilder", errorHandlerRef);
		} else if (getRoutesWithNoErrorHandler().contains(routeId)) {
			assertTrue(msg, StringUtils.isBlank(errorHandlerRef));
		} else {
			assertEquals(msg, "shutdownErrorHandler", errorHandlerRef);
		}
	}
	
}
