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
@Table(name = "providermanagement_provider_role")
@AttributeOverride(name = "id", column = @Column(name = "provider_role_id"))
public class ProviderManagementProviderLight extends AttributeTypeLight {
}
