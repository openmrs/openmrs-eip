package org.openmrs.sync.core.camel.load;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openmrs.sync.core.camel.StringToLocalDateTimeConverter;
import org.openmrs.sync.core.config.TestConfig;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = TestConfig.class)
public abstract class OpenMrsLoadEndpointITest {

    @Autowired
    protected CamelContext camelContext;

    @Produce(uri = "direct:startLoad")
    protected ProducerTemplate template;

    @Autowired
    private EntityServiceFacade facade;

    @Before
    public void init() throws Exception {
        camelContext.addComponent("openmrsLoad", new OpenMrsLoadComponent(camelContext, facade));
        camelContext.getTypeConverterRegistry().addTypeConverter(LocalDateTime.class, String.class, new StringToLocalDateTimeConverter());
        camelContext.addRoutes(createRouteBuilder());
    }

    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:startLoad")
                        .to("openmrsLoad");
            }
        };
    }
}
