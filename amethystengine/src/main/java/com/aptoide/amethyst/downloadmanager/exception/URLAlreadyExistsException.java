package com.aptoide.amethyst.downloadmanager.exception;

/**
 * An exception indicating an URL already exists.
 * @author Edward Larsson (edward.larsson@gmx.com)
 */
public class URLAlreadyExistsException extends Exception {

	/**
	 * Constructor for the exception.
	 */
	public URLAlreadyExistsException() {
		super();
	}

	/**
	 * Constructor with a message.
	 * @param message The message with details about the exception.
	 */
	public URLAlreadyExistsException(String message) {
		super(message);
	}
}
