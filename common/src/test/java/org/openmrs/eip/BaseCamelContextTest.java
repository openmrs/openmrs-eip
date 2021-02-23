package org.openmrs.eip;

import org.apache.camel.CamelContext;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
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
	protected CamelContext camelContext;
	
	@Produce
	protected ProducerTemplate producerTemplate;
	
}
