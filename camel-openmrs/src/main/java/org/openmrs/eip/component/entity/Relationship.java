package org.openmrs.eip.component.entity;

import java.time.LocalDateTime;

import org.openmrs.eip.component.entity.light.PersonLight;
import org.openmrs.eip.component.entity.light.RelationshipTypeLight;

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
@Table(name = "relationship")
@AttributeOverride(name = "id", column = @Column(name = "relationship_id"))
public class Relationship extends BaseChangeableDataEntity {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "person_a")
	private PersonLight persona;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "relationship")
	private RelationshipTypeLight relationshipType;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "person_b")
	private PersonLight personb;
	
	@Column(name = "start_date")
	protected LocalDateTime startDate;
	
	@Column(name = "end_date")
	protected LocalDateTime endDate;
}
