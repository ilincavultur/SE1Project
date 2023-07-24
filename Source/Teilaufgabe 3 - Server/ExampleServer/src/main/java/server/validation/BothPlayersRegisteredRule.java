package server.validation;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.PlayerMove;
import server.exceptions.NotEnoughPlayersException;
import server.game.GameData;
import server.game.GameStateController;
import server.player.Player;

// before halfmap can be received
public class BothPlayersRegisteredRule implements IRuleValidation{
	private static final Logger logger = LoggerFactory.getLogger(BothPlayersRegisteredRule.class);
	
	@Override
	public void validatePlayerReg(Map<String, GameData> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {}
	
	@Override
	public void validatePlayerId(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {}

	@Override
	public void validateGameId(Map<String, GameData> games, UniqueGameIdentifier gameId) {}

	@Override
	public void validateHalfMap(HalfMap halfMap) {}

	@Override
	public void validateGameState(GameStateController controller, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {
		Map<String, GameData> games = controller.getGames();
		List<Player> players = games.get(gameId.getUniqueGameID()).getPlayerController().getPlayers();
		int number = 0;

		for (Player pl: players) {
			if (pl.getPlayerId().isEmpty()) {
				throw new NotEnoughPlayersException("Only one client has registered", "Client tried to send half Map but not both players were registered");
			} else {
				number += 1;
			}
		}
		
		if (number != 2) {
			throw new NotEnoughPlayersException("Only one client has registered", "Client tried to send half Map but not both players were registered");
		}
		
	}

	@Override
	public void validateMove(Map<String, GameData> games, PlayerMove move, UniqueGameIdentifier gameId) {}

	@Override
	public void myTurn(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {}

}
