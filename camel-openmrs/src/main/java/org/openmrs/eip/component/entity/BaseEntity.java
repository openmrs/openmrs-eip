package org.openmrs.eip.component.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "uuid")
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
     * Tests if an entity is out of date compared the given entity
     *
     * @param entity entity to test
     * @return true if out of date
     */
    public abstract boolean wasModifiedAfter(BaseEntity entity);
}
