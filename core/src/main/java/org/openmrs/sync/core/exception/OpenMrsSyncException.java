package org.openmrs.sync.core.exception;

public class OpenMrsSyncException extends RuntimeException {

    public OpenMrsSyncException(final String s, final Throwable throwable) {
        super(s, throwable);
    }

    public OpenMrsSyncException(final String s) {
        super(s);
    }
}
