package server.gamestate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.HalfMap;
import server.controllers.GameStateController;
import server.exceptions.GameIdException;
import server.exceptions.HalfMapException;
import server.exceptions.NotPlayersTurnException;
import server.exceptions.PlayerIdException;
import server.exceptions.TooManyPlayersException;
import server.models.GameData;
import server.models.Player;
import server.validation.FortRule;
import server.validation.GameIdRule;
import server.validation.HalfMapSizeRule;
import server.validation.MaxNoOfPlayersReachedRule;
import server.validation.MyTurnRule;
import server.validation.PlayerIdRule;
import server.validation.TerrainsNumberRule;

public class GameStateTests {
	GameIdRule gameIdRule = new GameIdRule();
	PlayerIdRule playerIdRule = new PlayerIdRule();
	MyTurnRule myTurnRule = new MyTurnRule();
	MaxNoOfPlayersReachedRule twoPlayersRegisteredRule = new MaxNoOfPlayersReachedRule();
	GameStateController controller = new GameStateController();
	UniquePlayerIdentifier playerId = new UniquePlayerIdentifier(controller.createUniquePlayerId());
	UniqueGameIdentifier gameId = controller.createUniqueGameId();
	
	@Test
	public void GameIdNotFound_ExpectedGameIdRuleThrows() {
		
		//arrange
		Map<String, GameData> games = new HashMap<String, GameData>();
		
		//act
		GameIdException e = Assertions.assertThrows(GameIdException.class, () -> {
			gameIdRule.validateGameId(games, gameId);
		});
		
		//assert
		Assertions.assertEquals("Game Id not found: " + gameId.getUniqueGameID(), e.getMessage());
		
	}
	
	/*@Test
	public void PlayerIdNotFound_ExpectedPlayerIdRuleThrows() {
		
		//arrange
		Map<String, GameData> games = new HashMap<String, GameData>();
		GameData gameData = new GameData();
		List<Player> players = new ArrayList<Player>();
		Player newPlayer = new Player();
		newPlayer.setPlayerId("asdadasdasdasd");
		players.add(newPlayer);
		gameData.setPlayers(players);
		games.put(gameId.getUniqueGameID(), gameData);
		
		//act
		PlayerIdException e = Assertions.assertThrows(PlayerIdException.class, () -> {
			playerIdRule.validatePlayerId(games, playerId, gameId);
		});
		
		//assert
		Assertions.assertEquals("Player id "+ playerId.getUniquePlayerID() + " not found in game: " + gameId.getUniqueGameID(), e.getMessage());
		
	}
	
	@Test
	public void NotMyTurn_ExpectedMyTurnRuleThrows() {
		
		//arrange
		Map<String, GameData> games = new HashMap<String, GameData>();
		GameData gameData = new GameData();
		
		List<Player> players = new ArrayList<Player>();
		Player newPlayer1 = new Player();
		newPlayer1.setPlayerId(controller.createUniquePlayerId().getUniquePlayerID());
		players.add(newPlayer1);
		Player newPlayer2 = new Player();
		newPlayer2.setPlayerId(controller.createUniquePlayerId().getUniquePlayerID());
		players.add(newPlayer2);
		gameData.setPlayers(players);
		games.put(gameId.getUniqueGameID(), gameData);
		
		//act
		NotPlayersTurnException e = Assertions.assertThrows(NotPlayersTurnException.class, () -> {
			if (gameData.getIdxPlayersTurn() == 0) {
				myTurnRule.myTurn(games, new UniquePlayerIdentifier(newPlayer2.getPlayerId()), gameId);	
			} else {
				myTurnRule.myTurn(games, new UniquePlayerIdentifier(newPlayer1.getPlayerId()), gameId);
			}
			
		});
		
		//assert
		if (gameData.getIdxPlayersTurn() == 0) {
			Assertions.assertEquals("Player: " + newPlayer2.getPlayerId() + " sent a move/map but it wasn't its turn", e.getMessage());	
		} else {
			Assertions.assertEquals("Player: " + newPlayer1.getPlayerId() + " sent a move/map but it wasn't its turn", e.getMessage());
		}
		
		
	}
	
	@Test
	public void PlayerRegistration_TwoPlayersAlreadyRegistered_ExpectedMaxNoOfPlayersReachedRuleThrows() {
		
		//arrange
		Map<String, GameData> games = new HashMap<String, GameData>();
		GameData gameData = new GameData();
		
		List<Player> players = new ArrayList<Player>();
		Player newPlayer1 = new Player();
		newPlayer1.setPlayerId(controller.createUniquePlayerId().getUniquePlayerID());
		players.add(newPlayer1);
		Player newPlayer2 = new Player();
		newPlayer2.setPlayerId(controller.createUniquePlayerId().getUniquePlayerID());
		players.add(newPlayer2);
		gameData.setPlayers(players);
		games.put(gameId.getUniqueGameID(), gameData);
		Player newPlayer3 = new Player();
		newPlayer3.setPlayerId(controller.createUniquePlayerId().getUniquePlayerID());
		
		
		//act
		TooManyPlayersException e = Assertions.assertThrows(TooManyPlayersException.class, () -> {
		
			twoPlayersRegisteredRule.validatePlayerReg(games, new UniquePlayerIdentifier(newPlayer3.getPlayerId()), gameId);
			
		});
		
		//assert
			Assertions.assertEquals("There are already 2 players registered for this game " + gameId.getUniqueGameID(), e.getMessage());
		
	}*/
}
