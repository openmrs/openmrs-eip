package org.openmrs.sync.core.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.core.entity.BaseEntity;

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
}
