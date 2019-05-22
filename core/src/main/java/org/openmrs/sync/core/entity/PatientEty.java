package org.openmrs.sync.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "patient")
@AttributeOverrides(
        {
                @AttributeOverride(name = "id", column = @Column(name = "patient_id"))
        }
)
public class PatientEty extends TimestampedEty {
}
