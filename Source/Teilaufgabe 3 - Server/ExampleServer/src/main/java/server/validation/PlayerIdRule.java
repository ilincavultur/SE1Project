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
import server.exceptions.PlayerIdException;
import server.models.Coordinates;
import server.models.GameState;
import server.models.InternalHalfMap;
import server.models.MapNode;
import server.models.Player;

// check if it s unique -> creation
// check if exists in the respective game
public class PlayerIdRule implements IRuleValidation {

	@Override
	public void validatePlayerReg(Map<String, GameState> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {
		boolean ok = false;
		for (Player player: games.get(gameId.toString()).getPlayers())
			if (player.getPlayerId().equals(playerId.toString())) {
				ok = true;
			}
		
		if (ok == false) {
			throw new PlayerIdException("Player Id Invalid", "Player id "+ playerId.toString() + " not found in game: " + gameId.toString());
		}
		
	}

	@Override
	public void validateHalfMap(HalfMap halfMap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateGameState(Map<String, GameState> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateMove(Map<String, GameState> games, PlayerMove move, UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void myTurn(Map<String, GameState> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}
	


}
