package org.openmrs.sync.component.common;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public abstract class AbstractSyncEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "date_created", updatable = false)
    private Date dateCreated;

    @NotBlank
    @Column(unique = true, length = 38, updatable = false)
    private String uuid;

    /**
     * Gets the id
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the dateCreated
     *
     * @return the dateCreated
     */
    public Date getDateCreated() {
        return dateCreated;
    }

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
     * Sets the dateCreated
     *
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || !getClass().isAssignableFrom(other.getClass())) {
            return false;
        }

        AbstractSyncEntity otherObj = (AbstractSyncEntity) other;
        if (getId() == null && otherObj.getId() == null) {
            return super.equals(other);
        }

        return getId().equals(otherObj.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " uuid: " + getUuid();
    }
}
