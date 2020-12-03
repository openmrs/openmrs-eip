package org.openmrs.eip.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.entity.light.PersonLight;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "person_name")
@Inheritance(strategy = InheritanceType.JOINED)
@AttributeOverride(name = "id", column = @Column(name = "person_name_id"))
public class PersonName extends BaseChangeableDataEntity {

    @NotNull
    @Column(name = "preferred")
    private boolean preferred;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "person_id")
    private PersonLight person;

    @Column(name = "prefix")
    private String prefix;

    @Column(name = "given_name")
    private String givenName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "family_name_prefix")
    private String familyNamePrefix;

    @Column(name = "family_name")
    private String familyName;

    @Column(name = "family_name2")
    private String familyName2;

    @Column(name = "family_name_suffix")
    private String familyNameSuffix2;

    @Column(name = "degree")
    private String degree;
}
