package org.openmrs.eip.component.repository;

public interface ObservationRepositoryCustom {

    /**
     * Transforms result from isObsInSectionActivityMySql into a boolean value as MySQL doesn't support boolean returns
     * @param uuid the uuid of the patient
     * @param conceptMapping the concept mapping
     * @return boolean
     */
    boolean isObsLinkedToGivenConceptMapping(String uuid,
                                             String conceptMapping);
}
