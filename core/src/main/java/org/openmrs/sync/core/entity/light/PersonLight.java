package org.openmrs.sync.core.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.core.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
@AttributeOverride(name = "id", column = @Column(name = "person_id"))
public class PersonLight extends BaseEntity {

    @Column(name = "creator")
    private Long creator;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;
}
