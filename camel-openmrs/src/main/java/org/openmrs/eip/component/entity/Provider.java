package org.openmrs.eip.component.entity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.utils.DateUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "provider")
@AttributeOverride(name = "id", column = @Column(name = "provider_id"))
public class Provider extends BaseCreatableEntity {
	
	@Column(name = "name")
    private String name;
	 
    @Column(name = "identifier")
    private String identifier;
	 
    /*@ManyToOne
    @JoinColumn(name = "provider_role_id")
    private ProviderManagementProviderRoleLight providerRole;*/

    @ManyToOne
    @JoinColumn(name = "role_id")
    private ConceptLight role;
    
    @ManyToOne
    @JoinColumn(name = "speciality_id")
    private ConceptLight speciality;
    
    @OneToOne
    @JoinColumn(name = "person_id")
    private PersonLight person;
    
    @ManyToOne
    @JoinColumn(name = "changed_by")
    private UserLight changedBy;

    @Column(name = "date_changed")
    private LocalDateTime dateChanged;

    @ManyToOne
    @JoinColumn(name = "retired_by")
    private UserLight retiredBy;

    @Column(name = "date_retired")
    private LocalDateTime dateRetired;

    @Column(name = "retire_reason")
    private String retireReason;

    @NotNull
    @Column(name = "retired")
    private boolean retired;
    
    @Override
    public boolean wasModifiedAfter(BaseEntity entity) {
        BaseChangeableMetaDataEntity other = (BaseChangeableMetaDataEntity) entity;
        List<LocalDateTime> dates = Arrays.asList(getDateChanged(), getDateRetired());
        List<LocalDateTime> otherDates = Arrays.asList(other.getDateChanged(), other.getDateRetired());
        return DateUtils.containsLatestDate(dates, otherDates);
    }

}
