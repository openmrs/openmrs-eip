package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class AttributeModel extends BaseChangeableDataModel {

    private String referencedEntityUuid;

    private String attributeTypeUuid;

    private String valueReference;

    /**
     * Gets the referencedEntityUuid
     *
     * @return the referencedEntityUuid
     */
    public String getReferencedEntityUuid() {
        return referencedEntityUuid;
    }

    /**
     * Sets the referencedEntityUuid
     *
     * @param referencedEntityUuid the referencedEntityUuid to set
     */
    public void setReferencedEntityUuid(String referencedEntityUuid) {
        this.referencedEntityUuid = referencedEntityUuid;
    }

    /**
     * Gets the attributeTypeUuid
     *
     * @return the attributeTypeUuid
     */
    public String getAttributeTypeUuid() {
        return attributeTypeUuid;
    }

    /**
     * Sets the attributeTypeUuid
     *
     * @param attributeTypeUuid the attributeTypeUuid to set
     */
    public void setAttributeTypeUuid(String attributeTypeUuid) {
        this.attributeTypeUuid = attributeTypeUuid;
    }

    /**
     * Gets the valueReference
     *
     * @return the valueReference
     */
    public String getValueReference() {
        return valueReference;
    }

    /**
     * Sets the valueReference
     *
     * @param valueReference the valueReference to set
     */
    public void setValueReference(String valueReference) {
        this.valueReference = valueReference;
    }
}
