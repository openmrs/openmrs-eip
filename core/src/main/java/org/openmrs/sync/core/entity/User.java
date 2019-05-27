package org.openmrs.sync.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@AttributeOverrides(
        {
                @AttributeOverride(name = "id", column = @Column(name = "user_id"))
        }
)
public class User extends BaseEntity {

    @NotNull
    private String systemId;

    @NotNull
    private Integer creator;

    @NotNull
    private LocalDateTime dateCreated;

    @NotNull
    private int personId;

    public User() {}
}
