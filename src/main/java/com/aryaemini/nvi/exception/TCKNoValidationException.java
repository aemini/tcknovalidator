package com.aryaemini.nvi.exception;

public class TCKNoValidationException extends Exception {

	private static final long serialVersionUID = 200L;
	private String message;
	private Throwable cause;

	public TCKNoValidationException() {}

	public TCKNoValidationException(String message) {
		this.message = message;
	}

	public TCKNoValidationException(String message, Throwable cause) {
		this.message = message;
		this.cause = cause;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	public Throwable getCause() {
		return this.cause;
	}

}
