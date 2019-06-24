package org.openmrs.sync.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.core.entity.light.ProviderAttributeTypeLight;
import org.openmrs.sync.core.entity.light.ProviderLight;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "provider_attribute")
@AttributeOverride(name = "id", column = @Column(name = "provider_attribute_id"))
public class ProviderAttribute extends Attribute<ProviderLight, ProviderAttributeTypeLight> {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ProviderLight referencedEntity;
}
