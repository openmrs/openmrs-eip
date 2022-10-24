package org.openmrs.eip.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class UserLight extends RetireableLightEntity {
	
	@NotNull
	@Column(name = "system_id")
	private String systemId;
	
	@NotNull
	@Column(name = "person_id")
	private Long personId;
}
