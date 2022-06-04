package server.validation;

import java.util.Map;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import server.exceptions.PlayerIdException;
import server.exceptions.TooManyPlayersException;
import server.models.Coordinates;
import server.models.GameData;
import server.models.MapNode;
import server.models.Player;

// if game already has 2 players registered do not let another one register
public class MaxNoOfPlayersReachedRule implements IRuleValidation{

	@Override
	public void validatePlayerReg(Map<String, GameData> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {
		if (games.get(gameId.getUniqueGameID()).getPlayers().size() == 2) {
			throw new TooManyPlayersException("Registration failed", "There are already 2 players registered for this game " + gameId.getUniqueGameID());
		}
		
	}
	
	// if player exists
	@Override
	public void validatePlayerId(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {
		
	}
	
	// if game exists
	@Override
	public void validateGameId(Map<String, GameData> games, UniqueGameIdentifier gameId) {
		
	}

	@Override
	public void validateHalfMap(HalfMap halfMap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateGameState(Map<String, GameData> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
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
