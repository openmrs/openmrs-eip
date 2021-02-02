package org.openmrs.eip.component.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.PersonLight;
import org.openmrs.eip.component.entity.light.ProviderManagementProviderLight;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "provider")
@AttributeOverride(name = "id", column = @Column(name = "provider_id"))
public class Provider extends BaseChangeableMetaDataEntity {
	
	@Column(name = "name")
    private String name;
	 
    @Column(name = "identifier")
    private String identifier;
	 
    @ManyToOne
    @JoinColumn(name = "provider_role_id")
    private ProviderManagementProviderLight providerRole;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private ConceptLight role;
    
    @ManyToOne
    @JoinColumn(name = "speciality_id")
    private ConceptLight speciality;
    
    @NotNull
    @OneToOne
    @JoinColumn(name = "person_id")
    private PersonLight person;
}
