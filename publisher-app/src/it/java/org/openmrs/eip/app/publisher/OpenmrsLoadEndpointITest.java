package org.openmrs.eip.app.publisher;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openmrs.eip.app.publisher.config.TestConfig;
import org.openmrs.eip.component.camel.OpenmrsComponent;
import org.openmrs.eip.component.camel.StringToLocalDateTimeConverter;
import org.openmrs.eip.component.service.security.PGPDecryptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.security.Security;
import java.time.LocalDateTime;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = TestConfig.class)
public abstract class OpenmrsLoadEndpointITest {

    @Autowired
    protected CamelContext camelContext;

    @Produce(property = "uri")
    protected ProducerTemplate template;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PGPDecryptService pgpDecryptService;

    public String getUri() {
        return "direct:start" + getClass().getSimpleName();
    }

    @Before
    public void init() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        camelContext.addComponent("openmrs", new OpenmrsComponent(camelContext, applicationContext));
        camelContext.getTypeConverterRegistry().addTypeConverter(LocalDateTime.class, String.class, new StringToLocalDateTimeConverter());
        camelContext.addRoutes(createRouteBuilder());
    }

    @After
    public void teardown() {
        camelContext.removeComponent("openmrs");
    }

    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from(getUri())
                        .process(pgpDecryptService)
                        .to("openmrs:load");
            }
        };
    }
}
