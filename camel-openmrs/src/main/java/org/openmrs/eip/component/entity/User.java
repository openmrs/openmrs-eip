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

import org.openmrs.eip.component.entity.light.PersonLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.utils.DateUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class User extends BaseCreatableEntity {
	
	@NotNull
	@Column(name = "system_id")
    private String systemId;
	 
    @Column(name = "username")
    private String username;
	 
    @Column(name = "password")
    private String password;
	 
    @Column(name = "salt")
    private String salt;
	 
    @Column(name = "secret_question")
    private String secretQuestion;

    @NotNull
    @OneToOne
    @JoinColumn(name = "person_id")
    private PersonLight person;
    
    @Column(name = "activation_key")
    private String activationKey;
    
    @Column(name = "email")
    private String email;
    
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
