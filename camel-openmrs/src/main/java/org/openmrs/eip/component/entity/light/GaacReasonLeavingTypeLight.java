package org.openmrs.eip.component.entity.light;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "gaac_reason_leaving_type")
@AttributeOverride(name = "id", column = @Column(name = "gaac_reason_leaving_type_id"))
public class GaacReasonLeavingTypeLight extends AttributeTypeLight {
}
