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
@Table(name = "program")
@AttributeOverride(name = "id", column = @Column(name = "program_id"))
public class ProgramLight extends LightEntity {
	
	@NotNull
	@Column(name = "name")
	private String name;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "concept_id")
	private ConceptLight concept;
	
	@Column(name = "retired")
	private boolean retired;
	
	@Override
	public void setMuted(final boolean muted) {
		this.retired = muted;
	}
	
	@Override
	public void setDateMuted(final LocalDateTime dateMuted) {
		// Not applicable
	}
	
	@Override
	public void setMuteReason(final String muteReason) {
		// Not applicable
	}
	
	@Override
	public void setMutedBy(final Long mutedBy) {
		// Not applicable
	}
}
