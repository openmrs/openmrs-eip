package org.openmrs.sync.component.model;

import java.time.LocalDateTime;

public class BaseMetadataModel extends BaseModel {

    private boolean retired;

    private String retiredByUuid;

    private LocalDateTime dateRetired;

    private String retiredReason;

    /**
     * Gets the retired
     *
     * @return the retired
     */
    public boolean isRetired() {
        return retired;
    }

    /**
     * Sets the retired
     *
     * @param retired the retired to set
     */
    public void setRetired(boolean retired) {
        this.retired = retired;
    }

    /**
     * Gets the retiredByUuid
     *
     * @return the retiredByUuid
     */
    public String getRetiredByUuid() {
        return retiredByUuid;
    }

    /**
     * Sets the retiredByUuid
     *
     * @param retiredByUuid the retiredByUuid to set
     */
    public void setRetiredByUuid(String retiredByUuid) {
        this.retiredByUuid = retiredByUuid;
    }

    /**
     * Gets the dateRetired
     *
     * @return the dateRetired
     */
    public LocalDateTime getDateRetired() {
        return dateRetired;
    }

    /**
     * Sets the dateRetired
     *
     * @param dateRetired the dateRetired to set
     */
    public void setDateRetired(LocalDateTime dateRetired) {
        this.dateRetired = dateRetired;
    }

    /**
     * Gets the retiredReason
     *
     * @return the retiredReason
     */
    public String getRetiredReason() {
        return retiredReason;
    }

    /**
     * Sets the retiredReason
     *
     * @param retiredReason the retiredReason to set
     */
    public void setRetiredReason(String retiredReason) {
        this.retiredReason = retiredReason;
    }

}
