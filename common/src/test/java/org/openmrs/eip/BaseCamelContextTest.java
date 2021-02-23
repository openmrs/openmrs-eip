package org.openmrs.eip;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base class for camel route tests and processors
 */
@RunWith(CamelSpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public abstract class BaseCamelContextTest {
	
	@Autowired
	protected DefaultCamelContext camelContext;
	
	@Produce
	protected ProducerTemplate producerTemplate;
	
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
	
}
