package com.oohtml.compiler;

/**
 * A standard exception class that isn't checked by the compiler. This makes
 * coding easier because you don't need to be constantly putting catch blocks
 * everywhere. All constructors are just super() calls.
 */
public class BadCodeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BadCodeException() {
		super();
	}

	public BadCodeException(String arg0) {
		super(arg0);
	}

	public BadCodeException(Throwable arg0) {
		super(arg0);
	}

	public BadCodeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public BadCodeException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
