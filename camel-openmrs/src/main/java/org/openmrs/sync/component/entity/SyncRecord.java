package org.openmrs.sync.component.entity;


import org.openmrs.sync.component.common.BaseStatefulEntity;
import org.openmrs.sync.component.common.Operation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "dbsync_sync_record")
public class SyncRecord extends BaseStatefulEntity {

    public static final long serialVersionUID = 1;

    @NotBlank
    @Column(name = "entity_id", updatable = false)
    private String entityId;

    @NotBlank
    @Column(name = "entity_table_name", updatable = false)
    private String entityTableName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 6, updatable = false)
    private Operation operation;

    /**
     * Gets the entityId
     *
     * @return the entityId
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * Sets the entityId
     *
     * @param entityId the entityId to set
     */
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    /**
     * Gets the entityTableName
     *
     * @return the entityTableName
     */
    public String getEntityTableName() {
        return entityTableName;
    }

    /**
     * Sets the entityTableName
     *
     * @param entityTableName the entityTableName to set
     */
    public void setEntityTableName(String entityTableName) {
        this.entityTableName = entityTableName;
    }

    /**
     * Gets the operation
     *
     * @return the operation
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * Sets the operation
     *
     * @param operation the operation to set
     */
    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "SyncRecord {entityTable=" + entityTableName + ", entityId=" + entityId + ", operation=" + operation +
                ", status=" + getStatus() + ", uuid=" + getUuid() + "}";
    }

}
