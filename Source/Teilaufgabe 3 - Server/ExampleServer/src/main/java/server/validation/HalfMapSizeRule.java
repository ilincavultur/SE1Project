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
import server.models.GameData;
import server.models.InternalHalfMap;
import server.models.MapNode;
import server.models.Player;

public class HalfMapSizeRule implements IRuleValidation{

	@Override
	public void validatePlayerReg(Map<String, GameData> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {}
	
	@Override
	public void validatePlayerId(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {}

	@Override
	public void validateGameId(Map<String, GameData> games, UniqueGameIdentifier gameId) {}

	@Override
	public void validateHalfMap(HalfMap halfMap) {
		if (halfMap.getMapNodes().size() != 32) {
			throw new HalfMapException("Invalid HalfMap Size", "32 Fields needed, but found" + halfMap.getMapNodes().size());
		}	
	}

	@Override
	public void validateGameState(Map<String, GameData> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {}

	@Override
	public void validateMove(Map<String, GameData> games, PlayerMove move, UniqueGameIdentifier gameId) {}

	@Override
	public void myTurn(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {}

}
