package server.validation;

import java.util.Map;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import server.enums.MoveCommand;
import server.exceptions.HalfMapException;
import server.models.Coordinates;
import server.models.GameState;
import server.models.InternalHalfMap;
import server.models.MapNode;
import server.models.Player;

public class HalfMapSizeRule implements IRuleValidation{

	@Override
	public void validatePlayerReg(Map<String, GameState> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateHalfMap(HalfMap halfMap) {
		if (halfMap.getMapNodes().size() != 32) {
			throw new HalfMapException("Invalid HalfMap Size", "32 Fields needed, but found" + halfMap.getMapNodes().size());
		}
		
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
