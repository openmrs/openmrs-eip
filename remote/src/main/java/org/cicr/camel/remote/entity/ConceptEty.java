package org.cicr.camel.remote.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "concept")
public class ConceptEty {

    @Id
    @GeneratedValue
    private int conceptId;

    @NotNull
    private String uuid;
}
