package commons;

public class BatchException extends Exception {

	public BatchException() {
	}

	public BatchException(String message) {
		super(message);
	}

	public BatchException(Throwable t) {
		super(t);
	}

	public BatchException(String message, Throwable t) {
		super(message, t);
	}

}
