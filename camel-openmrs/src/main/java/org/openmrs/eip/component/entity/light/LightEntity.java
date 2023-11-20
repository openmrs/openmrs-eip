package org.openmrs.eip.component.entity.light;

import java.time.LocalDateTime;

import org.openmrs.eip.component.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class LightEntity extends BaseEntity {
	
	@Column(name = "creator")
	private Long creator;
	
	@NotNull
	@Column(name = "date_created")
	private LocalDateTime dateCreated;
	
	public abstract void setMuted(boolean mute);
	
	public abstract void setDateMuted(LocalDateTime dateMuted);
	
	public abstract void setMuteReason(String muteReason);
	
	public abstract void setMutedBy(Long mutedBy);
	
	@Override
	public boolean wasModifiedAfter(final BaseEntity model) {
		return false;
	}
}
