package org.openmrs.eip.app;

import java.security.Security;
import java.time.LocalDateTime;

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openmrs.eip.app.config.TestConfig;
import org.openmrs.eip.component.camel.OpenmrsComponent;
import org.openmrs.eip.component.camel.StringToLocalDateTimeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public abstract class OpenmrsLoadEndpointITest {
	
	@Autowired
	protected CamelContext camelContext;
	
	@Produce(property = "uri")
	protected ProducerTemplate template;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	protected DataSource dataSource;
	
	public String getUri() {
		return "direct:start" + getClass().getSimpleName();
	}
	
	@Before
	public void init() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		
		camelContext.addComponent("openmrs", new OpenmrsComponent(camelContext, applicationContext));
		camelContext.getTypeConverterRegistry().addTypeConverter(LocalDateTime.class, String.class,
		    new StringToLocalDateTimeConverter());
		camelContext.addRoutes(createRouteBuilder());
		ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-data.sql"));
	}
	
	@After
	public void teardown() {
		camelContext.removeComponent("openmrs");
	}
	
	protected RouteBuilder createRouteBuilder() {
		return new RouteBuilder() {
			
			@Override
			public void configure() {
				from(getUri()).to("openmrs:load");
			}
		};
	}
}
