package server.validation;

import java.util.HashMap;
import java.util.Map;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import server.enums.MoveCommand;
import server.exceptions.GameIdException;
import server.exceptions.NotEnoughPlayersException;
import server.game.GameData;
import server.game.GameStateController;
import server.map.Coordinates;
import server.map.InternalHalfMap;
import server.map.MapNode;
import server.player.Player;

public class GameIdRule implements IRuleValidation {

	@Override
	public void validatePlayerReg(Map<String, GameData> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {}

	@Override
	public void validatePlayerId(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {}

	@Override
	public void validateGameId(Map<String, GameData> games, UniqueGameIdentifier gameId) {
		if (!games.containsKey(gameId.getUniqueGameID())) {
			throw new GameIdException("Game Id Invalid", "Game Id not found: " + gameId.getUniqueGameID());
		}
	}

	@Override
	public void validateHalfMap(HalfMap halfMap) {}

	@Override
	public void validateGameState(GameStateController controller, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {}

	@Override
	public void validateMove(Map<String, GameData> games, PlayerMove move, UniqueGameIdentifier gameId) {}

	@Override
	public void myTurn(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {}

}
