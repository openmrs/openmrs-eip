package org.openmrs.sync.app.management.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "sync_attempt")
public class SyncAttempt extends AbstractEntity {

    public static final long serialVersionUID = 1;

    public enum Status {
        SUCCESS, FAILURE
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "sync_record_id", updatable = false)
    private SyncRecord syncRecord;

    @Enumerated(EnumType.STRING)
    @Column(name = "wo_status", length = 50, nullable = false)
    private Status status;

    /**
     * Gets the syncRecord
     *
     * @return the syncRecord
     */
    public SyncRecord getSyncRecord() {
        return syncRecord;
    }

    /**
     * Sets the syncRecord
     *
     * @param syncRecord the syncRecord to set
     */
    public void setSyncRecord(SyncRecord syncRecord) {
        this.syncRecord = syncRecord;
    }

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
     * Determines is this attempt was successful
     *
     * @return true if the attempt was successful otherwise false
     */
    public boolean isSuccessful() {
        return Status.SUCCESS == getStatus();
    }

}
