package server.exceptions;

public class NotEnoughPlayersException extends GenericExampleException{

	public NotEnoughPlayersException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

}
