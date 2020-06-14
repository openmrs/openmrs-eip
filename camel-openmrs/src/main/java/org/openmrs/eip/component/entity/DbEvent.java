package org.openmrs.eip.component.entity;


import java.io.Serializable;

public class DbEvent implements Serializable {

    public static final long serialVersionUID = 1;

    private String entityId;

    private String entityTableName;

    private String operation;

    public DbEvent() {
    }

    public DbEvent(String entityId, String entityTableName, String operation) {
        this.entityId = entityId;
        this.entityTableName = entityTableName;
        this.operation = operation;
    }

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
    public String getOperation() {
        return operation;
    }

    /**
     * Sets the operation
     *
     * @param operation the operation to set
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "DbEvent {entityTable=" + entityTableName + ", entityId=" + entityId + ", operation=" + operation + "}";
    }

}
