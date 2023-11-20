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
@Table(name = "obs")
@AttributeOverride(name = "id", column = @Column(name = "obs_id"))
public class ObservationLight extends VoidableLightEntity {
	
	@NotNull
	@Column(name = "obs_datetime")
	private LocalDateTime obsDatetime;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "concept_id")
	private ConceptLight concept;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "person_id")
	private PersonLight person;
}
