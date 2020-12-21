package org.openmrs.eip.publisher.management.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "inbound_retry_queue")
public class InBoundRetryQueueItem extends BaseRetryQueueItem {

    public static final long serialVersionUID = 1;

    @Column(name = "model_class_name", nullable = false, updatable = false)
    private String modelClassName;

    //Unique identifier for the entity usually a uuid or name for an entity like a privilege that has no uuid
    @Column(nullable = false, updatable = false)
    private String identifier;

    @Column(name = "entity_payload", columnDefinition = "text", nullable = false)
    private String entityPayload;

    /**
     * Gets the modelClassName
     *
     * @return the modelClassName
     */
    public String getModelClassName() {
        return modelClassName;
    }

    /**
     * Sets the modelClassName
     *
     * @param modelClassName the modelClassName to set
     */
    public void setModelClassName(String modelClassName) {
        this.modelClassName = modelClassName;
    }

    /**
     * Gets the identifier
     *
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier
     *
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

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
        return "InBoundRetryQueueItem {route=" + getRoute() + ", identifier=" + identifier + ", modelClassName="
                + modelClassName + ", attemptCount=" + getAttemptCount() + ", payload=" + entityPayload + "}";
    }

}
