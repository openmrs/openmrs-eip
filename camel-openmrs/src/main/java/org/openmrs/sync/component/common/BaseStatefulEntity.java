package org.openmrs.sync.component.common;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.util.Date;

@MappedSuperclass
public abstract class BaseStatefulEntity extends AbstractSyncEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50) default 'NEW'")
    private Status status;

    @Column(name = "status_message")
    private String statusMessage;

    @Column(name = "sync_date")
    private Date syncDate;

    /**
     * Gets the status
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the status
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Gets the statusMessage
     *
     * @return the statusMessage
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the statusMessage
     *
     * @param statusMessage the statusMessage to set
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * Gets the syncDate
     *
     * @return the syncDate
     */
    public Date getSyncDate() {
        return syncDate;
    }

    /**
     * Sets the syncDate
     *
     * @param syncDate the syncDate to set
     */
    public void setSyncDate(Date syncDate) {
        this.syncDate = syncDate;
    }

    /**
     * Determines if the instance has been processed successful
     *
     * @return true if the instance was successfully processed otherwise false
     */
    public boolean isSuccessful() {
        return Status.SUCCESS == getStatus();
    }

}
