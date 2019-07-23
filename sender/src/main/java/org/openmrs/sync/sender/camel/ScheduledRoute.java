package org.openmrs.sync.sender.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ScheduledRoute extends RouteBuilder {

    private static final int DELAY = 96 * 60 * 60 * 1000;

    @Override
    public void configure() {
        from("scheduler:sync?delay=" + DELAY)
                .to("jpa://org.openmrs.sync.sender.management.entity.TableSyncStatus?" +
                        "query=select p from org.openmrs.sync.sender.management.entity.TableSyncStatus p")
                .split(body()).streaming()
                .setHeader("OpenMrsEntitySyncStatusId", simple("${in.body.getId()}"))
                .to("seda:sync");
    }
}
