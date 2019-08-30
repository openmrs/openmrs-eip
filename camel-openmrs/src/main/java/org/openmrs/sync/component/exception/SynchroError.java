package org.openmrs.sync.component.exception;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.openmrs.sync.component.model.BaseModel;

import java.time.LocalDateTime;

@Builder
@EqualsAndHashCode
@Getter
public class SynchroError {

    private LocalDateTime date;

    private BaseModel model;

    private Throwable cause;
}
