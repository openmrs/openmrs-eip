package org.openmrs.eip.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class LightEntity extends BaseEntity {

    @Column(name = "creator")
    private Long creator;

    @NotNull
    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    public abstract void setMuted(boolean mute);

    public abstract void setDateMuted(LocalDateTime dateMuted);

    public abstract void setMuteReason(String muteReason);

    public abstract void setMutedBy(Long mutedBy);

    @Override
    public boolean wasModifiedAfter(final BaseEntity model) {
        return false;
    }
}
