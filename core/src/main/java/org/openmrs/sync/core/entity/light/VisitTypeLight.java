package org.openmrs.sync.core.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "visit_type")
@AttributeOverride(name = "id", column = @Column(name = "visit_type_id"))
@AttributeOverride(name = "voided", column = @Column(name = "retired"))
@AttributeOverride(name = "voidReason", column = @Column(name = "retire_reason"))
@AttributeOverride(name = "dateVoided", column = @Column(name = "date_retired"))
public class VisitTypeLight extends LightEntity {

    @NotNull
    @Column(name = "name")
    private String name;
}
