package org.openmrs.eip;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * Base class for camel route tests and processors
 */
@RunWith(CamelSpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@TestExecutionListeners(value = { DirtiesContextBeforeModesTestExecutionListener.class, MockitoTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        ResetMocksTestExecutionListener.class })
@TestPropertySource(properties = "logging.config=classpath:logback-test.xml")
@TestPropertySource(properties = "camel.component.direct.block=false")
@TestPropertySource(properties = "logging.level.org.openmrs.eip=DEBUG")
@DirtiesContext
public abstract class BaseCamelTest {
	
	protected static final Logger log = LoggerFactory.getLogger(BaseCamelTest.class);
	
	@Autowired
	protected ApplicationContext applicationContext;
	
	@Autowired
	protected DefaultCamelContext camelContext;
	
	@Produce
	protected ProducerTemplate producerTemplate;
	
	private LoggerContext loggerContext;
	
	protected void advise(String routeId, AdviceWithRouteBuilder builder) throws Exception {
		camelContext.adviceWith(camelContext.getRouteDefinition(routeId), builder);
	}
	
	@Before
	public void beforeBaseCamelTest() throws Exception {
		loadXmlRoutesInDirectory("camel-common", "test-error-handler.xml");
		if (loggerContext == null) {
			loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		}
		
		((ListAppender) loggerContext.getLogger(ROOT_LOGGER_NAME).getAppender("test")).list.clear();
	}
	
	protected void assertMessageLogged(Level level, String message) {
		ListAppender<LoggingEvent> app = (ListAppender) loggerContext.getLogger(ROOT_LOGGER_NAME).getAppender("test");
		List<LoggingEvent> list = app.list;
		for (LoggingEvent e : list) {
			if (e.getLevel().equals(level) && e.getMessage().equalsIgnoreCase(message)) {
				log.info("Log event satisfied -> [" + level + "] " + message);
				return;
			}
		}
		
		Assert.fail("Log event not satisfied -> [" + level + "] " + message);
	}
	
	/**
	 * Loads and registers the routes defined in the files on the classpath with the specified names.
	 *
	 * @param filenames the names of the files to load
	 * @throws Exception
	 */
	private void loadXmlRoutes(String... filenames) throws Exception {
		for (String file : filenames) {
			InputStream in = getClass().getClassLoader().getResourceAsStream(file);
			RoutesDefinition rd = (RoutesDefinition) camelContext.getXMLRoutesDefinitionLoader()
			        .loadRoutesDefinition(camelContext, in);
			camelContext.addRouteDefinitions(rd.getRoutes());
		}
	}
	
	/**
	 * Loads and registers the routes defined in files with the specified names from the camel directory
	 * on the classpath.
	 * 
	 * @param filenames the names of the files to load
	 * @throws Exception
	 */
	protected void loadXmlRoutesInCamelDirectory(String... filenames) throws Exception {
		loadXmlRoutes(Arrays.stream(filenames).map(f -> Paths.get("camel", f).toString()).toArray(String[]::new));
	}
	
	/**
	 * Loads and registers the routes defined in files with the specified names from the specified
	 * directory on the classpath.
	 *
	 * @param directory the name of the directory containing the routes
	 * @param filenames the names of the files to load
	 * @throws Exception
	 */
	protected void loadXmlRoutesInDirectory(String directory, String... filenames) throws Exception {
		loadXmlRoutes(Arrays.stream(filenames).map(f -> Paths.get(directory, f).toString()).toArray(String[]::new));
	}
	
	protected String getErrorMessage(Exchange e) {
		return e.getProperty("error", Exception.class).getMessage();
	}
	
}
