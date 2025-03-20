package dscatalog.services.exceptions;

public class SourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SourceNotFoundException (String msg) {
		super(msg);
	}
	
}
