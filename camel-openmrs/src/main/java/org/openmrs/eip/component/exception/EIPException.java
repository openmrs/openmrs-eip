package org.openmrs.eip.component.exception;

public class EIPException extends RuntimeException {
	
	public EIPException(final String s, final Throwable throwable) {
		super(s, throwable);
	}
	
	public EIPException(final String s) {
		super(s);
	}
}
