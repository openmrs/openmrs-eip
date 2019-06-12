package org.openmrs.sync.core.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "form")
@AttributeOverride(name = "id", column = @Column(name = "form_id"))
public class FormLight extends LightEntity {

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "version")
    private String version;
}
