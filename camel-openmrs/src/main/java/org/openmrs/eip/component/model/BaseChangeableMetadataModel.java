package org.openmrs.eip.component.model;

import java.time.LocalDateTime;

public abstract class BaseChangeableMetadataModel extends BaseMetadataModel {

    private String changedByUuid;

    private LocalDateTime dateChanged;

    /**
     * Gets the changedByUuid
     *
     * @return the changedByUuid
     */
    public String getChangedByUuid() {
        return changedByUuid;
    }

    /**
     * Sets the changedByUuid
     *
     * @param changedByUuid the changedByUuid to set
     */
    public void setChangedByUuid(String changedByUuid) {
        this.changedByUuid = changedByUuid;
    }

    /**
     * Gets the dateChanged
     *
     * @return the dateChanged
     */
    public LocalDateTime getDateChanged() {
        return dateChanged;
    }

    /**
     * Sets the dateChanged
     *
     * @param dateChanged the dateChanged to set
     */
    public void setDateChanged(LocalDateTime dateChanged) {
        this.dateChanged = dateChanged;
    }
}
