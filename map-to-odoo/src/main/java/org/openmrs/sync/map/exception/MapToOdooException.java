package org.openmrs.sync.map.exception;

public class MapToOdooException extends RuntimeException {

    public MapToOdooException(final String s) {
        super(s);
    }

    public MapToOdooException(final String s, final Throwable throwable) {
        super(s, throwable);
    }
}
