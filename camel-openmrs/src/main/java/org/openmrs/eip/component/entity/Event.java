package org.openmrs.eip.component.entity;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Map;

@Embeddable
public class Event implements Serializable {

    public static final long serialVersionUID = 1;

    //Unique identifier for the entity usually a uuid or name for an entity like a privilege that has no uuid
    private String identifier;

    //The primary key value of the affected row
    private String primaryKeyId;

    private String tableName;

    private String operation;

    private Boolean snapshot = Boolean.FALSE;

    @Transient
    private Map<String, Object> previousState;

    @Transient
    private Map<String, Object> currentState;

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
     * Gets the primaryKeyId
     *
     * @return the primaryKeyId
     */
    public String getPrimaryKeyId() {
        return primaryKeyId;
    }

    /**
     * Sets the primaryKeyId
     *
     * @param primaryKeyId the primaryKeyId to set
     */
    public void setPrimaryKeyId(String primaryKeyId) {
        this.primaryKeyId = primaryKeyId;
    }

    /**
     * Gets the tableName
     *
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets the tableName
     *
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
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

    /**
     * Gets the snapshot
     *
     * @return the snapshot
     */
    public Boolean getSnapshot() {
        return snapshot;
    }

    /**
     * Sets the snapshot
     *
     * @param snapshot the snapshot to set
     */
    public void setSnapshot(Boolean snapshot) {
        this.snapshot = snapshot;
    }

    /**
     * Gets the previousState
     *
     * @return the previousState
     */
    public Map<String, Object> getPreviousState() {
        return previousState;
    }

    /**
     * Sets the previousState
     *
     * @param previousState the previousState to set
     */
    public void setPreviousState(Map<String, Object> previousState) {
        this.previousState = previousState;
    }

    /**
     * Gets the currentState
     *
     * @return the currentState
     */
    public Map<String, Object> getCurrentState() {
        return currentState;
    }

    /**
     * Sets the currentState
     *
     * @param currentState the currentState to set
     */
    public void setCurrentState(Map<String, Object> currentState) {
        this.currentState = currentState;
    }

    @Override
    public String toString() {
        return "Event {tableName=" + tableName + ", primaryKeyId=" + primaryKeyId + ", identifier=" + identifier
                + ", operation=" + operation + ", snapshot=" + snapshot + "}";
    }

}
