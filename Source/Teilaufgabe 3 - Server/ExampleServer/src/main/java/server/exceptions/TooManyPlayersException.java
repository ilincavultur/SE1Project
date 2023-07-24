package server.exceptions;

public class TooManyPlayersException extends GenericExampleException{

	public TooManyPlayersException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

}
