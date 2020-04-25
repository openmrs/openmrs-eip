package org.openmrs.sync.app.management.entity;

import org.openmrs.sync.component.common.AbstractSyncEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "openmrs_db")
public class OpenmrsDatabase extends AbstractSyncEntity {

    @NotBlank
    @Column(unique = true, length = 38)
    private String name;

    /**
     * Gets the name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }

}