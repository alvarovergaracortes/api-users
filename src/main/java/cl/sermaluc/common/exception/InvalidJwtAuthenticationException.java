package cl.sermaluc.common.exception;

public class InvalidJwtAuthenticationException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public InvalidJwtAuthenticationException(String message) {
		super(message);
	}
}
