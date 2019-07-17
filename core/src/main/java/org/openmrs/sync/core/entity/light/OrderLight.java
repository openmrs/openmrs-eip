package org.openmrs.sync.core.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders")
@AttributeOverride(name = "id", column = @Column(name = "order_id"))
public class OrderLight extends VoidableLightEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "order_type_id")
    private OrderTypeLight orderType;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "concept_id")
    private ConceptLight concept;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "orderer")
    private ProviderLight orderer;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "encounter_id")
    private EncounterLight encounter;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientLight patient;

    @NotNull
    @Column(name = "order_number")
    private String orderNumber;

    @NotNull
    @Column(name = "order_action")
    private String orderAction;

    @NotNull
    @ManyToOne
    @JoinColumn
    private CareSettingLight careSetting;
}
