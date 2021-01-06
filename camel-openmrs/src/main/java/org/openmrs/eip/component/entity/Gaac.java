package org.openmrs.eip.component.entity;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.openmrs.eip.component.entity.light.FormLight;
import org.openmrs.eip.component.entity.light.LocationLight;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.VisitLight;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "gaac")
@AttributeOverride(name = "id", column = @Column(name = "gaac_id"))
public class Gaac extends AuditableEntity {

    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @NotNull
    @Column(name = "start_date")
    protected LocalDateTime startDate;
    
    @Column(name = "end_date")
    protected LocalDateTime endDate;
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "focal_patient_id")
    private PatientLight focalPatient;

    @Column(name = "affinity_type")
    private Integer affinityType;
    
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
