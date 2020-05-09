package org.openmrs.sync.component.model;

import lombok.EqualsAndHashCode;
import org.openmrs.sync.component.common.Address;

@EqualsAndHashCode(callSuper = true)
public class LocationModel extends BaseChangeableMetadataModel {

    private String name;

    private String description;

    private Address address;

    private String parentLocationUuid;

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

    /**
     * Gets the description
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the address
     *
     * @return the address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets the address
     *
     * @param address the address to set
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Gets the parentLocationUuid
     *
     * @return the parentLocationUuid
     */
    public String getParentLocationUuid() {
        return parentLocationUuid;
    }

    /**
     * Sets the parentLocationUuid
     *
     * @param parentLocationUuid the parentLocationUuid to set
     */
    public void setParentLocationUuid(String parentLocationUuid) {
        this.parentLocationUuid = parentLocationUuid;
    }
}
