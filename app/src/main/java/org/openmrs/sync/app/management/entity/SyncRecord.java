package org.openmrs.sync.app.management.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collection;
import java.util.LinkedHashSet;

@Entity
@Table(name = "sync_record")
public class SyncRecord extends AbstractEntity {

    public static final long serialVersionUID = 1;

    @Column(name = "entity_id", nullable = false, updatable = false)
    private String entityId;

    @Column(name = "entity_class_name", nullable = false, updatable = false)
    private String entityClassName;

    @OneToMany(mappedBy = "syncRecord", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Collection<SyncAttempt> attempts;

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
     * Gets the entityClassName
     *
     * @return the entityClassName
     */
    public String getEntityClassName() {
        return entityClassName;
    }

    /**
     * Sets the entityClassName
     *
     * @param entityClassName the entityClassName to set
     */
    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }

    /**
     * Gets the attempts
     *
     * @return the attempts
     */
    public Collection<SyncAttempt> getAttempts() {
        if (attempts == null) {
            attempts = new LinkedHashSet();
        }

        return attempts;
    }

    /**
     * Sets the attempts
     *
     * @param attempts the attempts to set
     */
    public void setAttempts(Collection<SyncAttempt> attempts) {
        this.attempts = attempts;
    }

    /**
     * Checks if this record was successfully synced, typically this holds true if this record has at least one
     * successful attempt.
     *
     * @return true if the record was successfully synced otherwise false
     */
    public boolean isSuccessful() {
        return getAttempts().stream().anyMatch(attempt -> attempt.isSuccessful());
    }

}
