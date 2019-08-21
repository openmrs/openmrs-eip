package org.openmrs.sync.common.marshalling;

public class MarshallingException extends RuntimeException {

    public MarshallingException(final String s, final Throwable throwable) {
        super(s, throwable);
    }
}
