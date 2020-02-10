package it.polito.dp2.BIB.ass2;

public class UnknownItemException extends Exception {

	public UnknownItemException() {
		// TODO Auto-generated constructor stub
	}

	public UnknownItemException(String message) {
		super(message);
	}

	public UnknownItemException(Throwable cause) {
		super(cause);
	}

	public UnknownItemException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownItemException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
