package org.openmrs.eip.component.entity;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.openmrs.eip.component.entity.light.GaacFamilyLite;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.RelationshipLight;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "gaac_family_member")
@AttributeOverride(name = "id", column = @Column(name = "family_member_id"))
public class GaacFamilyMember extends AuditableEntity {
    @NotNull
    @ManyToOne
    @JoinColumn(name = "family_id")
    private GaacFamilyLite family;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "member_id")
    private PatientLight member;
    
    @ManyToOne
    @JoinColumn(name = "family_member_relacao")
    private RelationshipLight familyMemberRelacao;
    
    @Column(name = "start_date")
    protected LocalDateTime startDate;
    
    @Column(name = "end_date")
    protected LocalDateTime endDate;
    
    @Column(name = "reason_leaving_type")
    private Integer reasonLeavingType;
    
    @Column(name = "leaving")
    private Boolean leaving;
    
    @Column(name = "restart")
    private Boolean restart;
    
    @Column(name = "restart_date")
    protected LocalDateTime restartDate;
 }
