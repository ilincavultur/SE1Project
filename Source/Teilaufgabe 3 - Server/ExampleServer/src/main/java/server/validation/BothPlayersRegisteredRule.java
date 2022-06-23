package server.validation;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.PlayerMove;
import server.controllers.GameStateController;
import server.exceptions.NotEnoughPlayersException;
import server.models.GameData;
import server.models.Player;

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
		List<Player> players = games.get(gameId.getUniqueGameID()).getPlayers();

		for (int i=0; i<players.size(); i++) {
			if (players.get(i).getPlayerId().equals("")) {
				throw new NotEnoughPlayersException("Only one client has registered", "Client tried to send half Map but not both players were registered");
			}
		}
		
	}

	@Override
	public void validateMove(Map<String, GameData> games, PlayerMove move, UniqueGameIdentifier gameId) {}

	@Override
	public void myTurn(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {}

}
