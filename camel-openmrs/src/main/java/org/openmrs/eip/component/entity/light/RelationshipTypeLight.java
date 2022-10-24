
package org.openmrs.eip.component.entity.light;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "relationship_type")
@AttributeOverride(name = "id", column = @Column(name = "relationship_type_id"))
public class RelationshipTypeLight extends RetireableLightEntity {
	
	@NotNull
	@Column(name = "a_is_to_b")
	private String aIsToB;
	
	@NotNull
	@Column(name = "b_is_to_a")
	private String bIsToA;
	
	@NotNull
	@Column(name = "weight")
	private Integer weight;
	
	@NotNull
	@Column(name = "preferred")
	private Boolean preferred;
}
