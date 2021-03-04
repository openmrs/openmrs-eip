package org.openmrs.eip.component.entity;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.openmrs.eip.component.entity.light.LocationLight;
import org.openmrs.eip.component.entity.light.PatientLight;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "gaac_family")
@AttributeOverride(name = "id", column = @Column(name = "family_id"))
public class GaacFamily extends BaseChangeableDataEntity {
	@NotNull
    @Column(name = "family_identifier")
    private String familyIdentifier;

    @NotNull
    @Column(name = "start_date")
    protected LocalDateTime startDate;
    
    @Column(name = "end_date")
    protected LocalDateTime endDate;
    
    @ManyToOne
    @JoinColumn(name = "focal_patient_id")
    private PatientLight focalPatient;
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationLight location;

    @NotNull
    @Column(name = "crumbled")
    private Boolean crumbled;
    
    @Column(name = "reason_crumbled")
    private String reasonCrumbled;
   
    @Column(name = "date_crumbled")
    protected LocalDateTime dateCrumbled;
}
