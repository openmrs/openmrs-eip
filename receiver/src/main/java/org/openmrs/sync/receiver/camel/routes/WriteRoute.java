package org.openmrs.sync.receiver.camel.routes;

import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;

import org.openmrs.sync.core.camel.RemoveFileTagsExpression;
import org.openmrs.sync.core.camel.TypeEnum;
import org.openmrs.sync.core.service.security.PGPDecryptService;
import org.springframework.stereotype.Component;

@Component
public class WriteRoute extends RouteBuilder {

    private PGPDecryptService pgpDecryptService;

    private RemoveFileTagsExpression removeFileTagsExpression;

    public WriteRoute(final PGPDecryptService pgpDecryptService,
                      final RemoveFileTagsExpression removeFileTagsExpression) {
        this.pgpDecryptService = pgpDecryptService;
        this.removeFileTagsExpression = removeFileTagsExpression;
    }

    @Override
    public void configure() {
        from("{{camel.input.endpoint}}")
                .convertBodyTo(String.class)
                .process(pgpDecryptService)
                .choice()
                        .when(PredicateBuilder.and(body().startsWith(TypeEnum.FILE.getOpeningTag()), body().endsWith(TypeEnum.FILE.getClosingTag())))
                                .transform(removeFileTagsExpression)
                                .unmarshal().base64()
                                .to("file:/home/sco/Desktop/res")
                        .otherwise()
                                .to("openmrsLoad");
    }
}
