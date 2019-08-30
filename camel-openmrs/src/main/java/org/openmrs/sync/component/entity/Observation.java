package org.openmrs.sync.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.component.entity.light.*;
import org.openmrs.sync.component.utils.DateUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "obs")
@AttributeOverride(name = "id", column = @Column(name = "obs_id"))
public class Observation extends BaseEntity {

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

    @ManyToOne
    @JoinColumn(name = "creator")
    protected UserLight creator;

    @NotNull
    @Column(name = "date_created")
    protected LocalDateTime dateCreated;

    @NotNull
    @Column(name = "voided")
    protected boolean voided;

    @ManyToOne
    @JoinColumn(name = "voided_by")
    protected UserLight voidedBy;

    @Column(name = "date_voided")
    protected LocalDateTime dateVoided;

    @Column(name = "void_reason")
    protected String voidReason;

    @Override
    public boolean wasModifiedAfter(final BaseEntity entity) {
        Observation observation = (Observation) entity;
        List<LocalDateTime> datesToCheck = Arrays.asList(
                observation.getDateCreated(),
                observation.getDateVoided());
        boolean dateCreatedAfter = DateUtils.isDateAfterAtLeastOneInList(getDateCreated(), datesToCheck);
        boolean dateVoidedAfter = DateUtils.isDateAfterAtLeastOneInList(getDateVoided(), datesToCheck);
        return dateCreatedAfter || dateVoidedAfter;
    }
}
