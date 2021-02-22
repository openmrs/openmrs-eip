package org.openmrs.eip.component.entity;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.openmrs.eip.component.entity.light.GaacAffinityTypeLight;
import org.openmrs.eip.component.entity.light.LocationLight;
import org.openmrs.eip.component.entity.light.PatientLight;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "gaac")
@AttributeOverride(name = "id", column = @Column(name = "gaac_id"))
public class Gaac extends BaseChangeableDataEntity {
	
	@NotNull
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "gaac_identifier")
    private String gaac_identifier;
    
    @NotNull
    @Column(name = "start_date")
    protected LocalDateTime startDate;
    
    @Column(name = "end_date")
    protected LocalDateTime endDate;
    
    @ManyToOne
    @JoinColumn(name = "focal_patient_id")
    private PatientLight focalPatient;

    @ManyToOne
    @JoinColumn(name = "affinity_type")
    private GaacAffinityTypeLight affinityType;
    		
    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationLight location;

    @Column(name = "crumbled")
    private Integer crumbled;
    
    @Column(name = "reason_crumbled")
    private String reasonCrumbled;
   
    @Column(name = "date_crumbled")
    protected LocalDateTime dateCrumbled;
}
