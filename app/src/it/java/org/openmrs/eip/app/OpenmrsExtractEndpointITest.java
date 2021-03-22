package org.openmrs.eip.app;

import lombok.Builder;
import lombok.Data;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openmrs.eip.app.config.TestConfig;
import org.openmrs.eip.component.camel.OpenmrsComponent;
import org.openmrs.eip.component.camel.StringToLocalDateTimeConverter;
import org.openmrs.eip.component.service.security.PGPDecryptService;
import org.openmrs.eip.component.service.security.PGPEncryptService;
import org.openmrs.eip.component.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.security.Security;
import java.time.LocalDateTime;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = TestConfig.class)
public abstract class OpenmrsExtractEndpointITest {

    @Autowired
    protected CamelContext camelContext;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(property = "uri")
    protected ProducerTemplate template;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    protected PGPDecryptService pgpDecryptService;

    @Autowired
    private PGPEncryptService pgpEncryptService;

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
        resultEndpoint.getExchanges().clear();
    }

    protected RoutesBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from(getUri())
                        .recipientList(simple("openmrs:extract?tableToSync=${body.getTableToSync()}&lastSyncDate=${body.getLastSyncDateAsString()}"))
                        .split().jsonpathWriteAsString("$").streaming()
                        .process(pgpEncryptService)
                        .to("log:json")
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
