package org.cicr.sync.core.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "concept")
public class ConceptEty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer conceptId;

    @NotNull
    private String uuid;

    @NotNull
    private int datatypeId;

    @NotNull
    private int classId;

    @NotNull
    private int creator;

    @NotNull
    private String dateCreated;

    public ConceptEty() {}
}
