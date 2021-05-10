package org.openmrs.eip.web.receiver;

import static org.apache.camel.impl.engine.DefaultFluentProducerTemplate.on;

import java.util.List;
import java.util.Map;

import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.web.BaseRestController;
import org.openmrs.eip.web.RestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestConstants.API_PATH + "/dbsync/receiver/conflict")
public class ConflictController extends BaseRestController {

    private static final Logger log = LoggerFactory.getLogger(ConflictController.class);

    @Override
    public Class<?> getClazz() {
        return ConflictQueueItem.class;
    }

    @GetMapping
    public List<Object> getAll() {
        if (log.isDebugEnabled()) {
            log.debug("Fetching conflicts");
        }

        return on(camelContext).to("jpa:" + getClazz().getSimpleName() + "?query=SELECT c from " + getClazz().getSimpleName()
                + " c WHERE c.resolved = false").request(List.class);
    }

    @GetMapping("/{id}")
    public Object get(@PathVariable("id") Integer id) {
        if (log.isDebugEnabled()) {
            log.debug("Fetching conflict with id: " + id);
        }

        return doGet(id);
    }

    @PatchMapping("/{id}")
    public Object update(@RequestBody Map<String, Object> payload, @PathVariable("id") Integer id) {
        if (log.isDebugEnabled()) {
            log.debug("Updating conflict with id: " + id);
        }

        //Currently the only update allowed is marking the conflict as resolved
        ConflictQueueItem conflict = (ConflictQueueItem) doGet(id);
        conflict.setResolved(Boolean.valueOf(payload.get("resolved").toString()));

        return on(camelContext).withBody(conflict).to("jpa:" + getClazz().getSimpleName()).request();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        if (log.isDebugEnabled()) {
            log.debug("Deleting conflict with id: " + id);
        }

        doDelete(id);
    }

}
