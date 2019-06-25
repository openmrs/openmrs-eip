package org.openmrs.sync.core.camel.extract;

import lombok.Builder;
import lombok.Data;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openmrs.sync.core.camel.StringToLocalDateTimeConverter;
import org.openmrs.sync.core.config.TestConfig;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;
import org.openmrs.sync.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class OpenMrsExtractEndpointITest {

    @Autowired
    protected CamelContext camelContext;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Autowired
    private EntityServiceFacade facade;

    @Before
    public void init() throws Exception {
        camelContext.addComponent("openmrsExtract", new OpenMrsExtractComponent(camelContext, facade));
        camelContext.getTypeConverterRegistry().addTypeConverter(LocalDateTime.class, String.class, new StringToLocalDateTimeConverter());
        camelContext.addRoutes(createRouteBuilder());
    }

    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .recipientList(simple("openmrsExtract:${body.getTableToSync()}?lastSyncDate=${body.getLastSyncDateAsString()}"))
                        .to("mock:result");
            }
        };
    }

    @Data
    @Builder
    public static class CamelInitObect {
        private LocalDateTime lastSyncDate;
        private String tableToSync;

        public String getLastSyncDateAsString() {
            return DateUtils.dateToString(getLastSyncDate());
        }
    }
}
