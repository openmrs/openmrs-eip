package org.openmrs.sync.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "icrc_erp_work_order")
@AttributeOverride(name = "id", column = @Column(name = "erp_work_order_id"))
public class ErpWorkOrderLight extends VoidableLightEntity {
}
