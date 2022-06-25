package server.game;

import java.time.Duration;
import java.time.Instant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import MessagesBase.MessagesFromServer.FullMap;
import MessagesBase.MessagesFromServer.GameState;
import MessagesBase.MessagesFromServer.PlayerState;
import server.enums.MapFieldType;
import server.enums.TreasureState;
import server.map.Coordinates;
import server.map.InternalHalfMap;
import server.map.MapNode;
import server.network.NetworkConverter;
import server.player.Player;

public class GameStateController {

	private Map<String, GameData> games = new HashMap<String, GameData>();
	private static int MAX_GAME_NUMBER = 999;
	private static final Logger logger = LoggerFactory.getLogger(GameStateController.class);

	public GameStateController() {
		super();	
	}

	public Map<String, GameData> getGames() {
		return games;
	}

	public UniqueGameIdentifier createUniqueGameId() {
		String newGameId = "";
		Random randomNo = new Random();
		String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int no = alphabet.length();
		
		while (newGameId.equals("") || games.containsKey(newGameId)) {
			for (int i=0; i<5; i++) {
				int nextCharacter = randomNo.nextInt(no);
				newGameId += alphabet.charAt(nextCharacter);
			}
		}

		return new UniqueGameIdentifier(newGameId);
	}
	
	public UniquePlayerIdentifier createUniquePlayerId() {		
		return new UniquePlayerIdentifier(UUID.randomUUID().toString());
	}
	
	public String getOldestGameId() {
		String oldestGameId = "";
		Duration longestDuration = Duration.ZERO;
		
		for (var eachGame : this.games.entrySet()) {
			Duration gameDuration = Duration.between(eachGame.getValue().getGameCreationTime(), Instant.now());
			
			if (gameDuration.compareTo(longestDuration) >= 0) {
				longestDuration = gameDuration;
				oldestGameId = eachGame.getKey();
			}
		}
		
		return oldestGameId;
	}
	
	public void removeGame(String gameId) {
		this.games.remove(gameId);
	}

	public void updateGameStateId(UniqueGameIdentifier gameID) {
		this.games.get(gameID.getUniqueGameID()).updateGameStateId();
	}
	
	public void createNewGame(UniqueGameIdentifier gameId) {
		
		// first remove oldest game if no more space
		if (this.games.size() >= MAX_GAME_NUMBER) {
			String oldestGameId = getOldestGameId();
			removeGame(oldestGameId);
		}
		
		this.games.put(gameId.getUniqueGameID(), new GameData(gameId.getUniqueGameID()));
	}

	public void registerPlayer (UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId, PlayerRegistration playerReg) {
		this.games.get(gameId.getUniqueGameID()).registerPlayer(playerId, playerReg);
	}
	
	public void receiveHalfMap(InternalHalfMap halfMap, String playerId, String gameId) {
		this.games.get(gameId).getPlayerWithId(playerId).receiveHalfMap(halfMap, gameId);
	}
	
	public void swapPlayerOnTurn(UniqueGameIdentifier gameID) {  
		this.games.get(gameID.getUniqueGameID()).swapPlayerOnTurn();
	}

	public boolean bothHalfMapsPresent(UniqueGameIdentifier gameID) {
		return this.games.get(gameID.getUniqueGameID()).bothHalfMapsPresent(gameID);
	}

	public GameState requestGameState(UniquePlayerIdentifier playerID, UniqueGameIdentifier gameID, NetworkConverter networkConverter) {
		return this.games.get(gameID.getUniqueGameID()).requestGameState(playerID, gameID, networkConverter);
	}

	public void receiveMove(UniqueGameIdentifier gameID, PlayerMove move, NetworkConverter networkConverter) {
		this.games.get(gameID.getUniqueGameID()).receiveMove(gameID, move, networkConverter);
	}
}
