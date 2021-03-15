package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class VisitAttributeModel extends AttributeModel {

    private String patientUuid;

    private String visitTypeUuid;

    /**
     * Gets the patientUuid
     *
     * @return the patientUuid
     */
    public String getPatientUuid() {
        return patientUuid;
    }

    /**
     * Sets the patientUuid
     *
     * @param patientUuid the patientUuid to set
     */
    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    /**
     * Gets the visitTypeUuid
     *
     * @return the visitTypeUuid
     */
    public String getVisitTypeUuid() {
        return visitTypeUuid;
    }

    /**
     * Sets the visitTypeUuid
     *
     * @param visitTypeUuid the visitTypeUuid to set
     */
    public void setVisitTypeUuid(String visitTypeUuid) {
        this.visitTypeUuid = visitTypeUuid;
    }
}
