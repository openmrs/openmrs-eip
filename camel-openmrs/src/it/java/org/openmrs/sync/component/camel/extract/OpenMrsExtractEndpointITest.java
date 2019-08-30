package org.openmrs.sync.component.camel.extract;

import lombok.Builder;
import lombok.Data;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openmrs.sync.component.camel.StringToLocalDateTimeConverter;
import org.openmrs.sync.component.camel.extract.fetchmodels.FetchModelsRuleEngine;
import org.openmrs.sync.component.config.TestConfig;
import org.openmrs.sync.component.service.security.PGPDecryptService;
import org.openmrs.sync.component.service.security.PGPEncryptService;
import org.openmrs.sync.component.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Security;
import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public abstract class OpenMrsExtractEndpointITest {

    @Autowired
    protected CamelContext camelContext;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:startExtract")
    protected ProducerTemplate template;

    @Autowired
    protected PGPDecryptService pgpDecryptService;

    @Autowired
    private FetchModelsRuleEngine ruleEngine;

    @Autowired
    private PGPEncryptService pgpEncryptService;

    @Before
    public void init() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        camelContext.addComponent("openmrsExtract", new OpenMrsExtractComponent(camelContext, ruleEngine));
        camelContext.getTypeConverterRegistry().addTypeConverter(LocalDateTime.class, String.class, new StringToLocalDateTimeConverter());
        camelContext.addRoutes(createRouteBuilder());
    }

    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:startExtract")
                        .recipientList(simple("openmrsExtract:${body.getTableToSync()}?lastSyncDate=${body.getLastSyncDateAsString()}"))
                        .split(body()).streaming()
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
