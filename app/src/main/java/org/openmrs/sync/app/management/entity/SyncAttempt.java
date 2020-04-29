package org.openmrs.sync.app.management.entity;

import org.openmrs.sync.component.common.BaseStatefulEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "sync_attempt")
public class SyncAttempt extends BaseStatefulEntity {

    public static final long serialVersionUID = 1;

    @NotBlank
    @Column(name = "sync_record_uuid", length = 38, updatable = false)
    private String syncRecordUuid;

    /**
     * Gets the syncRecordUuid
     *
     * @return the syncRecordUuid
     */
    public String getSyncRecordUuid() {
        return syncRecordUuid;
    }

    /**
     * Sets the syncRecordUuid
     *
     * @param syncRecordUuid the syncRecordUuid to set
     */
    public void setSyncRecordUuid(String syncRecordUuid) {
        this.syncRecordUuid = syncRecordUuid;
    }

    @Override
    public String toString() {
        return "SyncAttempt {status=" + getStatus() + ", syncRecordUuid=" + syncRecordUuid + ", uuid=" + getUuid() + "}";
    }

}
