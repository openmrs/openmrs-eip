package org.openmrs.eip.publisher.management.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "conflict_queue")
public class ConflictQueueItem extends AbstractEntity {

    public static final long serialVersionUID = 1;

    @Column(name = "model_class_name", nullable = false, updatable = false)
    private String modelClassName;

    //Unique identifier for the entity usually a uuid or name for an entity like a privilege that has no uuid
    @Column(nullable = false, updatable = false)
    private String identifier;

    @Column(name = "entity_payload", columnDefinition = "text", nullable = false)
    private String entityPayload;

    @Column(name = "is_resolved", nullable = false)
    private Boolean resolved = false;

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

    /**
     * Gets the resolved
     *
     * @return the resolved
     */
    public Boolean getResolved() {
        return resolved;
    }

    /**
     * Sets the resolved
     *
     * @param resolved the resolved to set
     */
    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }

    @Override
    public String toString() {
        return "ConflictQueueItem {identifier=" + identifier + ", modelClassName=" + modelClassName + ", payload=" +
                entityPayload + "}";
    }

}
