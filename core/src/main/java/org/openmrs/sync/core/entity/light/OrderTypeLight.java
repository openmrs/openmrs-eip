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
@Table(name = "order_type")
@AttributeOverride(name = "id", column = @Column(name = "order_type_id"))
@AttributeOverride(name = "voided", column = @Column(name = "retired"))
@AttributeOverride(name = "voidReason", column = @Column(name = "retire_reason"))
@AttributeOverride(name = "dateVoided", column = @Column(name = "date_retired"))
@AttributeOverride(name = "voidedBy", column = @Column(name = "retired_by"))
public class OrderTypeLight extends LightEntity {

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "java_class_name")
    private String javaClassName;
}
