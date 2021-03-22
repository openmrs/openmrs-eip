package org.openmrs.eip.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "form")
@AttributeOverride(name = "id", column = @Column(name = "form_id"))
@AttributeOverride(name = "retireReason", column = @Column(name = "retired_reason"))
public class FormLight extends RetireableLightEntity {

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "version")
    private String version;
}
