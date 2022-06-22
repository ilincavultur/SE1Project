package server.validation;

import java.util.Map;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import server.controllers.GameStateController;
import server.enums.MoveCommand;
import server.models.Coordinates;
import server.models.GameData;
import server.models.InternalHalfMap;
import server.models.MapNode;
import server.models.Player;

public interface IRuleValidation {

	public void validatePlayerReg(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId);
	
	public void validatePlayerId(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId);
	
	public void validateGameId(Map<String, GameData> games, UniqueGameIdentifier gameId);

	public void validateHalfMap(HalfMap halfMap);

	//public void validateGameState(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId);
	public void validateGameState(GameStateController controller, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId);

	public void validateMove(Map<String, GameData> games, PlayerMove move, UniqueGameIdentifier gameId);
	
	public void myTurn(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId);
}
