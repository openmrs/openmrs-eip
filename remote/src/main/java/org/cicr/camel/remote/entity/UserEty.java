package org.cicr.camel.remote.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "users")
public class UserEty {

    @Id
    @GeneratedValue
    private int userId;

    @NotNull
    private String uuid;
}
