package org.cicr.sync.core.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "users")
public class UserEty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @NotNull
    private String uuid;

    @NotNull
    private String systemId;

    @NotNull
    private int creator;

    @NotNull
    private String dateCreated;

    @NotNull
    private int personId;

    public UserEty() {}
}
