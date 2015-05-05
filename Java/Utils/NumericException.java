package utils;

/**
 *
 * @author Cristian
 */
public class NumericException extends RuntimeException {

	private NumericError error;

	public NumericError getError() {
		return error;
	}

	/*
	 * Constructors without NumericError argument
	 */
	public NumericException() {
		super();
	}

	public NumericException(String message) {
		super(message);
	}

	public NumericException(String message, Throwable cause) {
		super(message, cause);
	}

	public NumericException(Throwable cause) {
		super(cause);
	}

	/*
	 * Constructors with NumericError argument
	 */
	public NumericException(NumericError error) {
		this();
		this.error = error;
	}

	public NumericException(String message, NumericError error) {
		this(message);
		this.error = error;
	}

	public NumericException(String message, Throwable cause, NumericError error) {
		this(message, cause);
		this.error = error;
	}

	public NumericException(Throwable cause, NumericError error) {
		this(cause);
		this.error = error;
	}
}
