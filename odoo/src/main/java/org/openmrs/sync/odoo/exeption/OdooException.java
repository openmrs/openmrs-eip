package org.openmrs.sync.odoo.exeption;

public class OdooException extends RuntimeException {

    public OdooException(final String s) {
        super(s);
    }

    public OdooException(final String s, final Throwable throwable) {
        super(s, throwable);
    }
}
