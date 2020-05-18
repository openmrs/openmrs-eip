package org.openmrs.sync.component.entity;

import org.openmrs.sync.component.entity.light.UserLight;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * OpenMRS data model distinguishes between data and metadata, please refer to the javadocs in OpenMRS on
 * BaseOpenmrsData and BaseOpenmrsMetadata classes, this is the superclass for classes in this project that represent
 * data entities.
 */
@MappedSuperclass
public abstract class BaseDataEntity extends BaseEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "creator")
    protected UserLight creator;

    @NotNull
    @Column(name = "date_created")
    protected LocalDateTime dateCreated;

    @NotNull
    @Column(name = "voided")
    private boolean voided;

    @ManyToOne
    @JoinColumn(name = "voided_by")
    private UserLight voidedBy;

    @Column(name = "date_voided")
    private LocalDateTime dateVoided;

    @Column(name = "void_reason")
    private String voidReason;

    /**
     * Gets the creator
     *
     * @return the creator
     */
    public UserLight getCreator() {
        return creator;
    }

    /**
     * Sets the creator
     *
     * @param creator the creator to set
     */
    public void setCreator(UserLight creator) {
        this.creator = creator;
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

    /**
     * Gets the voided
     *
     * @return the voided
     */
    public boolean isVoided() {
        return voided;
    }

    /**
     * Sets the voided
     *
     * @param voided the voided to set
     */
    public void setVoided(boolean voided) {
        this.voided = voided;
    }

    /**
     * Gets the voidedBy
     *
     * @return the voidedBy
     */
    public UserLight getVoidedBy() {
        return voidedBy;
    }

    /**
     * Sets the voidedBy
     *
     * @param voidedBy the voidedBy to set
     */
    public void setVoidedBy(UserLight voidedBy) {
        this.voidedBy = voidedBy;
    }

    /**
     * Gets the dateVoided
     *
     * @return the dateVoided
     */
    public LocalDateTime getDateVoided() {
        return dateVoided;
    }

    /**
     * Sets the dateVoided
     *
     * @param dateVoided the dateVoided to set
     */
    public void setDateVoided(LocalDateTime dateVoided) {
        this.dateVoided = dateVoided;
    }

    /**
     * Gets the voidReason
     *
     * @return the voidReason
     */
    public String getVoidReason() {
        return voidReason;
    }

    /**
     * Sets the voidReason
     *
     * @param voidReason the voidReason to set
     */
    public void setVoidReason(String voidReason) {
        this.voidReason = voidReason;
    }
}
