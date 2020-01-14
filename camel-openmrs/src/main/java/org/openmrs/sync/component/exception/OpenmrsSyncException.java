package org.openmrs.sync.component.exception;

public class OpenmrsSyncException extends RuntimeException {

    public OpenmrsSyncException(final String s, final Throwable throwable) {
        super(s, throwable);
    }

    public OpenmrsSyncException(final String s) {
        super(s);
    }
}
