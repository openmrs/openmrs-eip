package org.openmrs.sync.component.common;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class BaseStatefulEntity extends AbstractSyncEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(updatable = false, columnDefinition = "varchar(50) default 'NEW'")
    private Status status;

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
     * Determines if the instance has been processed successful
     *
     * @return true if the instance was successfully processed otherwise false
     */
    public boolean isSuccessful() {
        return Status.SUCCESS == getStatus();
    }

}
