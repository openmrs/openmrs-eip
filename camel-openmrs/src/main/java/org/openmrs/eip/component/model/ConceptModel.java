package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ConceptModel extends BaseChangeableMetadataModel {

    private String datatypeUuid;

    private String conceptClassUuid;

    private String shortName;

    private String description;

    private String formText;

    private String version;

    private boolean isSet;

    /**
     * Gets the datatypeUuid
     *
     * @return the datatypeUuid
     */
    public String getDatatypeUuid() {
        return datatypeUuid;
    }

    /**
     * Sets the datatypeUuid
     *
     * @param datatypeUuid the datatypeUuid to set
     */
    public void setDatatypeUuid(String datatypeUuid) {
        this.datatypeUuid = datatypeUuid;
    }

    /**
     * Gets the conceptClassUuid
     *
     * @return the conceptClassUuid
     */
    public String getConceptClassUuid() {
        return conceptClassUuid;
    }

    /**
     * Sets the conceptClassUuid
     *
     * @param conceptClassUuid the conceptClassUuid to set
     */
    public void setConceptClassUuid(String conceptClassUuid) {
        this.conceptClassUuid = conceptClassUuid;
    }

    /**
     * Gets the shortName
     *
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the shortName
     *
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
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
     * Gets the formText
     *
     * @return the formText
     */
    public String getFormText() {
        return formText;
    }

    /**
     * Sets the formText
     *
     * @param formText the formText to set
     */
    public void setFormText(String formText) {
        this.formText = formText;
    }

    /**
     * Gets the version
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version
     *
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the isSet
     *
     * @return the isSet
     */
    public boolean isSet() {
        return isSet;
    }

    /**
     * Sets the set
     *
     * @param set the set to set
     */
    public void setSet(boolean set) {
        isSet = set;
    }
}
