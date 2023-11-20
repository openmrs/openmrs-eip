package org.openmrs.eip.component.entity.light;

import java.time.LocalDateTime;

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
@Table(name = "gaac_family")
@AttributeOverride(name = "id", column = @Column(name = "family_id"))
public class GaacFamilyLight extends VoidableLightEntity {
	
	@NotNull
	@Column(name = "family_identifier")
	private String familyIdentifier;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "location_id")
	private LocationLight location;
	
	@NotNull
	@Column(name = "crumbled")
	private Integer crumbled;
	
	@NotNull
	@Column(name = "start_date")
	protected LocalDateTime startDate;
}
