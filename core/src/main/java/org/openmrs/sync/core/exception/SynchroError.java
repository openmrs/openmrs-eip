package org.openmrs.sync.core.exception;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.openmrs.sync.core.model.BaseModel;

import java.time.LocalDateTime;

@Builder
@EqualsAndHashCode
@Getter
public class SynchroError {

    private LocalDateTime date;

    private BaseModel model;

    private Throwable cause;
}
