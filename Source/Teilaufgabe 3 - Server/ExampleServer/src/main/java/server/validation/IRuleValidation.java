package server.validation;

import java.util.Map;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import server.enums.MoveCommand;
import server.game.GameData;
import server.game.GameStateController;
import server.map.Coordinates;
import server.map.InternalHalfMap;
import server.map.MapNode;
import server.player.Player;

public interface IRuleValidation {

	public void validatePlayerReg(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId);
	
	public void validatePlayerId(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId);
	
	public void validateGameId(Map<String, GameData> games, UniqueGameIdentifier gameId);

	public void validateHalfMap(HalfMap halfMap);

	public void validateGameState(GameStateController controller, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId);

	public void validateMove(Map<String, GameData> games, PlayerMove move, UniqueGameIdentifier gameId);
	
	public void myTurn(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId);
}
