package org.openmrs.eip.app.management.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "inbound_retry_queue")
public class InBoundRetryQueueItem extends BaseRetryQueueItem {

    private static final Logger logger = LoggerFactory.getLogger(InBoundRetryQueueItem.class);

    @Column(name = "entity_payload", columnDefinition = "text")
    private String entityPayload;

    /**
     * Gets the entityPayload
     *
     * @return the entityPayload
     */
    public String getEntityPayload() {
        return entityPayload;
    }

    /**
     * Sets the entityPayload
     *
     * @param entityPayload the entityPayload to set
     */
    public void setEntityPayload(String entityPayload) {
        this.entityPayload = entityPayload;
    }

    @Override
    public String toString() {
        return "InBoundRetryItem {route=" + getRoute() + ", attemptCount=" + getAttemptCount() + ", payload=" + entityPayload + "}";
    }

}
