package org.openmrs.eip.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.ConceptNameLight;
import org.openmrs.eip.component.entity.light.DrugLight;
import org.openmrs.eip.component.entity.light.EncounterLight;
import org.openmrs.eip.component.entity.light.LocationLight;
import org.openmrs.eip.component.entity.light.ObservationLight;
import org.openmrs.eip.component.entity.light.OrderLight;
import org.openmrs.eip.component.entity.light.PersonLight;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "obs")
@AttributeOverride(name = "id", column = @Column(name = "obs_id"))
public class Observation extends BaseDataEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "person_id")
    private PersonLight person;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "concept_id")
    private ConceptLight concept;

    @ManyToOne
    @JoinColumn(name = "encounter_id")
    private EncounterLight encounter;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderLight order;

    @NotNull
    @Column(name = "obs_datetime")
    private LocalDateTime obsDatetime;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationLight location;

    @ManyToOne
    @JoinColumn(name = "obs_group_id")
    private ObservationLight obsGroup;

    @Column(name = "accession_number")
    private String accessionNumber;

    @Column(name = "value_group_id")
    private Long valueGroupId;

    @ManyToOne
    @JoinColumn(name = "value_coded")
    private ConceptLight valueCoded;

    @ManyToOne
    @JoinColumn(name = "value_coded_name_id")
    private ConceptNameLight valueCodedName;

    @ManyToOne
    @JoinColumn(name = "value_drug")
    private DrugLight valueDrug;

    @Column(name = "value_datetime")
    private LocalDateTime valueDatetime;

    @Column(name = "value_numeric")
    private Double valueNumeric;

    @Column(name = "value_modifier")
    private Integer valueModifier;

    @Column(name = "value_text")
    private String valueText;

    @Column(name = "value_complex")
    private String valueComplex;

    @Column(name = "comments")
    private String comments;

    @OneToOne
    @JoinColumn(name = "previous_version")
    private ObservationLight previousVersion;

    @Column(name = "form_namespace_and_path")
    private String formNamespaceAndPath;

    @Column(name = "status")
    private String status;

    @Column(name = "interpretation")
    private String interpretation;

}
