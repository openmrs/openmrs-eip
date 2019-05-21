package org.cicr.sync.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "users")
@AttributeOverrides(
        {
                @AttributeOverride(name = "id", column = @Column(name = "user_id"))
        }
)
public class UserEty extends OpenMrsEty {

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
