package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class OrderFrequencyModel extends BaseChangeableMetadataModel {

    private Double frequencyPerDay;

    private String conceptUuid;

    /**
     * Gets the frequencyPerDay
     *
     * @return the frequencyPerDay
     */
    public Double getFrequencyPerDay() {
        return frequencyPerDay;
    }

    /**
     * Sets the frequencyPerDay
     *
     * @param frequencyPerDay the frequencyPerDay to set
     */
    public void setFrequencyPerDay(Double frequencyPerDay) {
        this.frequencyPerDay = frequencyPerDay;
    }

    /**
     * Gets the conceptUuid
     *
     * @return the conceptUuid
     */
    public String getConceptUuid() {
        return conceptUuid;
    }

    /**
     * Sets the conceptUuid
     *
     * @param conceptUuid the conceptUuid to set
     */
    public void setConceptUuid(String conceptUuid) {
        this.conceptUuid = conceptUuid;
    }

}
