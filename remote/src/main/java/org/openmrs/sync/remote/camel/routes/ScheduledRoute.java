package org.openmrs.sync.remote.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ScheduledRoute extends RouteBuilder {

    private static final int DELAY = 60 * 1000;

    @Override
    public void configure() {
        from("scheduler:sync?delay=" + DELAY)
                .to("jpa://org.openmrs.sync.remote.management.entity.TableSyncStatus?" +
                        "query=select p from org.openmrs.sync.remote.management.entity.TableSyncStatus p")
                .split(body()).streaming()
                .setHeader("OpenMrsTableSyncStatusId", simple("${in.body.getId()}"))
                .to("seda:sync");
    }
}
