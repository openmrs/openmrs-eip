package org.openmrs.eip.component.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.openmrs.eip.component.entity.light.PersonLight;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "provider")
@AttributeOverride(name = "id", column = @Column(name = "provider_id"))
public class Provider extends MetaDataEntity {
	
	@Column(name = "name")
    private String name;
	 
    @Column(name = "identifier")
    private String identifier;
	 
    @Column(name = "provider_role_id")
    private Integer providerRoleId;

    @Column(name = "role_id")
    private Integer roleId;
    
    @Column(name = "speciality_id")
    private Integer specialityId;
    
    @NotNull
    @OneToOne
    @JoinColumn(name = "person_id")
    private PersonLight person;
}
