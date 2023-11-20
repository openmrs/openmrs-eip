package org.openmrs.eip.component.entity.light;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "care_setting")
@AttributeOverride(name = "id", column = @Column(name = "care_setting_id"))
public class CareSettingLight extends RetireableLightEntity {
	
	@NotNull
	@Column(name = "name")
	private String name;
	
	@NotNull
	@Column(name = "care_setting_type")
	private String careSettingType;
}
