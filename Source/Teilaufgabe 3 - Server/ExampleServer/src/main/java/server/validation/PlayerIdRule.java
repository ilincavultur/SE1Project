package server.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import server.enums.MoveCommand;
import server.exceptions.GameIdException;
import server.exceptions.PlayerIdException;
import server.models.Coordinates;
import server.models.GameData;
import server.models.InternalHalfMap;
import server.models.MapNode;
import server.models.Player;

// check if exists in the respective game
public class PlayerIdRule implements IRuleValidation {

	@Override
	public void validatePlayerReg(Map<String, GameData> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {
		
		
	}
	
	// if player exists
	@Override
	public void validatePlayerId(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {
		boolean ok = false;
		for (Player player: games.get(gameId.getUniqueGameID()).getPlayers())
			if (player.getPlayerId().equals(playerId.getUniquePlayerID())) {
				ok = true;
			}
		
		if (ok == false) {
			throw new PlayerIdException("Player Id Invalid", "Player id "+ playerId.getUniquePlayerID() + " not found in game: " + gameId.getUniqueGameID());
		}
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
