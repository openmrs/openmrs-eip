package org.openmrs.eip.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.entity.light.PersonAttributeTypeLight;
import org.openmrs.eip.component.entity.light.PersonLight;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "person_attribute")
@AttributeOverride(name = "id", column = @Column(name = "person_attribute_id"))
public class PersonAttribute extends BaseChangeableDataEntity {

    @NotNull
    @Column(name = "value")
    private String value;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "person_id")
    private PersonLight person;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "person_attribute_type_id")
    private PersonAttributeTypeLight personAttributeType;
}
