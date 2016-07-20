package excepciones;

public class OeeException extends Exception {
	String error;

	public OeeException() {
		super();
		error = "unknown";
	}

	public OeeException(String err) {
		super(err);
		error = err;
	}

	public String getError() {
		return error;
	}
}
