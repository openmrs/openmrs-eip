package org.openmrs.eip.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class VoidableLightEntity extends LightEntity {
	
	@Column(name = "voided")
	private boolean voided;
	
	@Column(name = "void_reason")
	private String voidReason;
	
	@Column(name = "date_voided")
	private LocalDateTime dateVoided;
	
	@Column(name = "voided_by")
	private Long voidedBy;
	
	@Override
	public void setMuted(final boolean muted) {
		this.voided = muted;
	}
	
	@Override
	public void setDateMuted(final LocalDateTime dateMuted) {
		this.dateVoided = dateMuted;
	}
	
	@Override
	public void setMuteReason(final String muteReason) {
		this.voidReason = muteReason;
	}
	
	@Override
	public void setMutedBy(final Long mutedBy) {
		this.voidedBy = mutedBy;
	}
}
