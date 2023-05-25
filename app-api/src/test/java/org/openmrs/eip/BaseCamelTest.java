package org.openmrs.eip;

import static java.io.File.separator;
import static org.openmrs.eip.app.SyncConstants.FOLDER_DIST;
import static org.openmrs.eip.app.SyncConstants.FOLDER_ROUTES;
import static org.powermock.reflect.Whitebox.setInternalState;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openmrs.eip.app.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
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
@TestPropertySource(properties = "openmrs.eip.log.level=DEBUG")
@TestPropertySource(properties = "logging.level.org.openmrs.eip=DEBUG")
@TestPropertySource(properties = "spring.liquibase.enabled=false")
@TestPropertySource(properties = "shutdown.notice.email.attachment.log.file=")
@TestPropertySource(properties = "smtp.host.name=")
@TestPropertySource(properties = "smtp.host.port=")
@TestPropertySource(properties = "smtp.auth.user=")
@TestPropertySource(properties = "smtp.auth.pass=")
@TestPropertySource(properties = "shutdown.notice.email.recipients=")
public abstract class BaseCamelTest {
	
	protected static final Logger log = LoggerFactory.getLogger(BaseCamelTest.class);
	
	@Autowired
	protected ApplicationContext applicationContext;
	
	@Autowired
	protected DefaultCamelContext camelContext;
	
	@Produce
	protected ProducerTemplate producerTemplate;
	
	private LoggerContext loggerContext;
	
	@Autowired
	protected ConfigurableEnvironment env;
	
	protected void advise(String routeId, AdviceWithRouteBuilder builder) throws Exception {
		camelContext.adviceWith(camelContext.getRouteDefinition(routeId), builder);
	}
	
	@BeforeClass
	public static void beforeBaseCamelTestClass() {
		//Reset in case previous tests set this to true
		setInternalState(AppUtils.class, "appContextStopping", false);
	}
	
	@Before
	public void beforeBaseCamelTest() throws Exception {
		loadClasspathXmlRoutes("test-error-handler.xml");
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
	private void loadClasspathXmlRoutes(String... filenames) throws Exception {
		for (String file : filenames) {
			loadRoute(getClass().getClassLoader().getResourceAsStream(file));
		}
	}
	
	/**
	 * Loads and registers the routes defined in the specified directory with the specified names.
	 * 
	 * @param appDir the name of the application directory
	 * @param filenames the names of the files to load
	 * @throws Exception
	 */
	protected void loadXmlRoutes(String appDir, String... filenames) throws Exception {
		for (String filename : filenames) {
			try {
				loadRoute(new FileInputStream(Paths.get(FOLDER_DIST, appDir, FOLDER_ROUTES, filename).toFile()));
			}
			catch (FileNotFoundException e) {
				//We are in a submodule directory, go one directory up to the parent project directory
				String subPath = FOLDER_DIST + separator + appDir + separator + FOLDER_ROUTES + separator + filename;
				loadRoute(new FileInputStream(new File(SystemUtils.getUserDir().getParentFile(), subPath)));
			}
		}
	}
	
	private void loadRoute(InputStream in) throws Exception {
		RoutesDefinition rd = (RoutesDefinition) camelContext.getXMLRoutesDefinitionLoader()
		        .loadRoutesDefinition(camelContext, in);
		camelContext.addRouteDefinitions(rd.getRoutes());
	}
	
	protected String getErrorMessage(Exchange e) {
		return e.getProperty("error", Exception.class).getMessage();
	}
	
	protected Exception getException(Exchange e) {
		return e.getProperty("error", Exception.class);
	}
	
}
