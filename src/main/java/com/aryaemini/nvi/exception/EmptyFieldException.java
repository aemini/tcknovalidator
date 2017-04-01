package com.aryaemini.nvi.exception;

public class EmptyFieldException extends Exception {

	private static final long serialVersionUID = 100L;
	private String message;
	private Throwable cause;

	public EmptyFieldException() {}

	public EmptyFieldException(String message) {
		this.message = message;
	}

	public EmptyFieldException(String message, Throwable cause) {
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
