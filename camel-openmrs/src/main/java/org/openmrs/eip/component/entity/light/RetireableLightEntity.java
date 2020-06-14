package org.openmrs.eip.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class RetireableLightEntity extends LightEntity {

    @Column(name = "retired")
    private boolean retired;

    @Column(name = "retire_reason")
    private String retireReason;

    @Column(name = "date_retired")
    private LocalDateTime dateRetired;

    @Column(name = "retired_by")
    private Long retiredBy;

    @Override
    public void setMuted(final boolean muted) {
        this.retired = muted;
    }

    @Override
    public void setDateMuted(final LocalDateTime dateMuted) {
        this.dateRetired = dateMuted;
    }

    @Override
    public void setMuteReason(final String muteReason) {
        this.retireReason = muteReason;
    }

    @Override
    public void setMutedBy(final Long mutedBy) {
        this.retiredBy = mutedBy;
    }
}
