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
@Table(name = "order_type")
@AttributeOverride(name = "id", column = @Column(name = "order_type_id"))
public class OrderTypeLight extends RetireableLightEntity {
	
	@NotNull
	@Column(name = "name")
	private String name;
	
	@NotNull
	@Column(name = "java_class_name")
	private String javaClassName;
}
