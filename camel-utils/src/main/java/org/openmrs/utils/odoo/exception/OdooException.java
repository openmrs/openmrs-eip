package org.openmrs.utils.odoo.exception;

/**
 * Generic Odoo exception
 */
public class OdooException extends RuntimeException {

    public OdooException(final String s, final Throwable throwable) {
        super(s, throwable);
    }

    public OdooException(final String s) {
        super(s);
    }
}
