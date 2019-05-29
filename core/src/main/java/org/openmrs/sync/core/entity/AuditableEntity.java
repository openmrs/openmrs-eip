package org.openmrs.sync.core.entity;

import org.openmrs.sync.core.entity.light.UserLight;

import java.time.LocalDateTime;

public interface AuditableEntity {

    UserLight getCreator();

    void setCreator(final UserLight creator);

    LocalDateTime getDateCreated();

    void setDateCreated(final LocalDateTime dateCreated);

    UserLight getChangedBy();

    void setChangedBy(final UserLight changedBy);

    LocalDateTime getDateChanged();

    void setDateChanged(final LocalDateTime dateChanged);

    boolean isVoided();

    void setVoided(final boolean voided);

    UserLight getVoidedBy();

    void setVoidedBy(final UserLight voidedBy);

    LocalDateTime getDateVoided();

    void setDateVoided(final LocalDateTime dateVoided);

    String getVoidReason();

    void setVoidReason(final String voidReason);
}
