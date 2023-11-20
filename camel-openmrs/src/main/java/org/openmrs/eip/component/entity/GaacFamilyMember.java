package org.openmrs.eip.component.entity;

import java.time.LocalDateTime;

import org.openmrs.eip.component.entity.light.GaacFamilyLight;
import org.openmrs.eip.component.entity.light.GaacReasonLeavingTypeLight;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.RelationshipLight;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "gaac_family_member")
@AttributeOverride(name = "id", column = @Column(name = "family_member_id"))
public class GaacFamilyMember extends BaseChangeableDataEntity {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "family_id")
	private GaacFamilyLight family;
	
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
	
	@ManyToOne
	@JoinColumn(name = "reason_leaving_type")
	private GaacReasonLeavingTypeLight reasonLeavingType;
	
	@Column(name = "leaving")
	private Boolean leaving;
	
	@Column(name = "restart")
	private Boolean restart;
	
	@Column(name = "restart_date")
	protected LocalDateTime restartDate;
}
