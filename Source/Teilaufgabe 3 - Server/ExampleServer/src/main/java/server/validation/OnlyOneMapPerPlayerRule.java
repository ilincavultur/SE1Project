package server.validation;

import java.util.Map;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.PlayerMove;
import server.exceptions.TooManyMapsSentException;
import server.models.GameData;

public class OnlyOneMapPerPlayerRule implements IRuleValidation{

	@Override
	public void validatePlayerReg(Map<String, GameData> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validatePlayerId(Map<String, GameData> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateGameId(Map<String, GameData> games, UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateHalfMap(HalfMap halfMap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateGameState(Map<String, GameData> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {
		if (games.get(gameId.getUniqueGameID()).getPlayerWithId(playerId.getUniquePlayerID()).getHalfMap() != null) {
			throw new TooManyMapsSentException("Too many half maps sent", "Client " + playerId.getUniquePlayerID() + " tried to send more than one half map");
		}
		
	}

	@Override
	public void validateMove(Map<String, GameData> games, PlayerMove move, UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void myTurn(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}

}
