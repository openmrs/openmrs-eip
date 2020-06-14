package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class PersonNameModel extends BaseChangeableDataModel {

    private boolean preferred;

    private String personUuid;

    private String prefix;

    private String givenName;

    private String middleName;

    private String familyNamePrefix;

    private String familyName;

    private String familyName2;

    private String familyNameSuffix2;

    private String degree;

    /**
     * Gets the preferred
     *
     * @return the preferred
     */
    public boolean isPreferred() {
        return preferred;
    }

    /**
     * Sets the preferred
     *
     * @param preferred the preferred to set
     */
    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }

    /**
     * Gets the personUuid
     *
     * @return the personUuid
     */
    public String getPersonUuid() {
        return personUuid;
    }

    /**
     * Sets the personUuid
     *
     * @param personUuid the personUuid to set
     */
    public void setPersonUuid(String personUuid) {
        this.personUuid = personUuid;
    }

    /**
     * Gets the prefix
     *
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix
     *
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Gets the givenName
     *
     * @return the givenName
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * Sets the givenName
     *
     * @param givenName the givenName to set
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    /**
     * Gets the middleName
     *
     * @return the middleName
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the middleName
     *
     * @param middleName the middleName to set
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Gets the familyNamePrefix
     *
     * @return the familyNamePrefix
     */
    public String getFamilyNamePrefix() {
        return familyNamePrefix;
    }

    /**
     * Sets the familyNamePrefix
     *
     * @param familyNamePrefix the familyNamePrefix to set
     */
    public void setFamilyNamePrefix(String familyNamePrefix) {
        this.familyNamePrefix = familyNamePrefix;
    }

    /**
     * Gets the familyName
     *
     * @return the familyName
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Sets the familyName
     *
     * @param familyName the familyName to set
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    /**
     * Gets the familyName2
     *
     * @return the familyName2
     */
    public String getFamilyName2() {
        return familyName2;
    }

    /**
     * Sets the familyName2
     *
     * @param familyName2 the familyName2 to set
     */
    public void setFamilyName2(String familyName2) {
        this.familyName2 = familyName2;
    }

    /**
     * Gets the familyNameSuffix2
     *
     * @return the familyNameSuffix2
     */
    public String getFamilyNameSuffix2() {
        return familyNameSuffix2;
    }

    /**
     * Sets the familyNameSuffix2
     *
     * @param familyNameSuffix2 the familyNameSuffix2 to set
     */
    public void setFamilyNameSuffix2(String familyNameSuffix2) {
        this.familyNameSuffix2 = familyNameSuffix2;
    }

    /**
     * Gets the degree
     *
     * @return the degree
     */
    public String getDegree() {
        return degree;
    }

    /**
     * Sets the degree
     *
     * @param degree the degree to set
     */
    public void setDegree(String degree) {
        this.degree = degree;
    }
}
