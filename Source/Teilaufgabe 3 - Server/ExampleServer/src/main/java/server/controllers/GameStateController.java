package server.controllers;

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
import server.models.Coordinates;
import server.models.GameData;
import server.models.InternalHalfMap;
import server.models.MapNode;
import server.models.Player;
import server.network.NetworkConverter;

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
	
		for (Player player : this.games.get(gameID.getUniqueGameID()).getPlayers()) {
			if (player.getHalfMap().isEmpty()) {
				return false;
			}
		}
		
		return true;
	}
	
	public void assembleHalfMaps(UniqueGameIdentifier gameID) {
		GameData game = this.games.get(gameID.getUniqueGameID());
		this.games.get(gameID.getUniqueGameID()).getFullMap().assembleFullMap(game, game.getPlayers(), game.getPlayers().get(0).getHalfMap(), game.getPlayers().get(1).getHalfMap());
	}

	public GameState requestGameState(UniquePlayerIdentifier playerID, UniqueGameIdentifier gameID, NetworkConverter networkConverter) {
		
		GameData game = this.games.get(gameID.getUniqueGameID());
		Set<PlayerState> players = new HashSet<>();
		Optional<FullMap> map = Optional.empty();
		List<Player> registeredPlayers = game.getPlayers();
		String playerId = playerID.getUniquePlayerID();
		
		if (registeredPlayers.size() == 1) {
			
			Player player = registeredPlayers.get(0);
			players.add(networkConverter.convertPlayerTo(game, player, false));
			
			return new GameState(map, players, game.getGameStateId());
			
		} else if (registeredPlayers.size() == 2) {
			
			Player player1 = game.getPlayerWithId(playerId);
			Player player2 = game.getTheOtherPlayer(playerId);
			
			players.add(networkConverter.convertPlayerTo(game, player1, false));
			players.add(networkConverter.convertPlayerTo(game, player2, true));
			
			if (bothHalfMapsPresent(gameID)) {
				assembleHalfMaps(gameID);
				map = networkConverter.convertServerFullMapTo(playerID, game);
			} else {
				if (player1.isPlayersHalfMapPresent()) {
					map = networkConverter.convertIHalfMapToNetworkFullMap(player1, game);
					
				} else if (player2.isPlayersHalfMapPresent()) {
					map = networkConverter.convertIHalfMapToNetworkFullMap(player2, game);
				}	
			}		
		}
		
		return new GameState(map, players, game.getGameStateId());
	}

	public void receiveMove(UniqueGameIdentifier gameID, PlayerMove move, NetworkConverter networkConverter) {
		
		GameData game = this.games.get(gameID.getUniqueGameID());
		
		Player player = game.getPlayerWithId(move.getUniquePlayerID());
		
		player.processMove(game.getFullMap(), move, networkConverter);
		player.updateTreasureStatus(game);
		player.updateEnemyFortStatus(game);
		player.updateMountainViewStatus(game, game.getFullMap());

	}
}
