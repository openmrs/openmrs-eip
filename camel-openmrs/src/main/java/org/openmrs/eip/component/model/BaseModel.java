package org.openmrs.eip.component.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class BaseModel {

    private String uuid;

    private String creatorUuid;

    private LocalDateTime dateCreated;

    /**
     * Gets the uuid
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the uuid
     *
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the creatorUuid
     *
     * @return the creatorUuid
     */
    public String getCreatorUuid() {
        return creatorUuid;
    }

    /**
     * Sets the creatorUuid
     *
     * @param creatorUuid the creatorUuid to set
     */
    public void setCreatorUuid(String creatorUuid) {
        this.creatorUuid = creatorUuid;
    }

    /**
     * Gets the dateCreated
     *
     * @return the dateCreated
     */
    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    /**
     * Sets the dateCreated
     *
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

}
