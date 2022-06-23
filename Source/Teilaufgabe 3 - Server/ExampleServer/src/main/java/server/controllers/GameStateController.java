package server.controllers;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
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
import MessagesBase.MessagesFromClient.EMove;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import MessagesBase.MessagesFromServer.FullMap;
import MessagesBase.MessagesFromServer.GameState;
import MessagesBase.MessagesFromServer.PlayerState;
import server.enums.MapFieldType;
import server.enums.MoveCommand;
import server.enums.TreasureState;
import server.exceptions.MoveException;
import server.exceptions.NotEnoughPlayersException;
import server.models.Coordinates;
import server.models.GameData;
import server.models.InternalFullMap;
import server.models.InternalHalfMap;
import server.models.MapNode;
import server.models.Player;
import server.network.NetworkConverter;
import server.validation.BothPlayersRegisteredRule;
import server.validation.DontMoveIntoWaterRule;
import server.validation.DontMoveOutsideMapRule;
import server.validation.FieldsCoordinatesRule;
import server.validation.FortRule;
import server.validation.GameIdRule;
import server.validation.HalfMapSizeRule;
import server.validation.IRuleValidation;
import server.validation.MaxNoOfPlayersReachedRule;
import server.validation.MyTurnRule;
import server.validation.NoIslandsRule;
import server.validation.PlayerIdRule;
import server.validation.TerrainsNumberRule;
import server.validation.WaterOnEdgesRule;

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
		
		UniqueGameIdentifier toReturn = new UniqueGameIdentifier(newGameId);
		
		return toReturn;
	}
	
	public UniquePlayerIdentifier createUniquePlayerId() {
		
		UniquePlayerIdentifier toReturn = new UniquePlayerIdentifier(UUID.randomUUID().toString());
		
		return toReturn;
	}
	
	public String getOldestGameId() {
		String toReturn = "";
		Duration longestDur = Duration.ZERO;
		for (Entry<String, GameData> mapEntry : this.games.entrySet()) {
			Duration entryDur = Duration.between(mapEntry.getValue().getGameCreationTime(), Instant.now());
			if (entryDur.compareTo(longestDur) >= 0) {
				longestDur = entryDur;
				toReturn = mapEntry.getKey();
			}
		}
		
		return toReturn;
	}
	
	public void removeGame(String gameId) {
		this.games.remove(gameId);
	}

	public void updateGameStateId(UniqueGameIdentifier gameID) {
		String newGameStateId = UUID.randomUUID().toString();
		
		this.games.get(gameID.getUniqueGameID()).setGameStateId(newGameStateId);
	}
	
	public void createNewGame(UniqueGameIdentifier gameId) {
		
		if (this.games.size() >= MAX_GAME_NUMBER) {
			
			String oldestGameId = getOldestGameId();
			
			removeGame(oldestGameId);
			
		}
		
		GameData newGame = new GameData();
		newGame.setGameId(gameId.getUniqueGameID());
		this.games.put(gameId.getUniqueGameID(), newGame);
	}

	public void registerPlayer (UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId, PlayerRegistration playerReg) {
		Player newPlayer = new Player();
		newPlayer.setPlayerId(playerId.getUniquePlayerID());
		newPlayer.setPlayerReg(playerReg);
		List<Player> players = this.games.get(gameId.getUniqueGameID()).getPlayers();
		players.add(newPlayer);
		this.games.get(gameId.getUniqueGameID()).setPlayers(players);
	}
	
	public Coordinates placeTreasure(InternalHalfMap halfMap) {
	
		Map<Coordinates, MapNode> fields = halfMap.getFields();
		
		Random randomNo = new Random();
		
		int randomFortX = randomNo.nextInt(8);
		int randomFortY = randomNo.nextInt(4);
		
		Coordinates fortPos = new Coordinates(randomFortX, randomFortY);
		
		while(fields.get(fortPos).getFieldType() != MapFieldType.GRASS) {
			randomFortX = randomNo.nextInt(8);
			randomFortY = randomNo.nextInt(4);
			fortPos = new Coordinates(randomFortX, randomFortY);
		}
		
		
		return fortPos;
		
	}
	
	public void receiveHalfMap(InternalHalfMap halfMap, String playerId, String gameId) {
		List<Player> players = this.games.get(gameId).getPlayers();
	
		for (Player player: players) {
			if (player.getPlayerId().equals(playerId)) {
				player.setCurrPos(halfMap.getFortPos());
				Coordinates treasurePos = placeTreasure(halfMap);
				halfMap.getFields().get(treasurePos).setTreasureState(TreasureState.MYTREASURE);
				player.setHalfMap(halfMap);
			}
		}
	}
	
	public void swapPlayerOnTurn(UniqueGameIdentifier gameID) {
		GameData game = this.games.get(gameID.getUniqueGameID());
		game.swapPlayerOnTurn();
	}

	public boolean bothHalfMapsPresent(UniqueGameIdentifier gameID) {
	
		for (Player pl : this.games.get(gameID.getUniqueGameID()).getPlayers()) {
			if (pl.getHalfMap().isEmpty()) {
				return false;
			}
		}
		
		return true;
	}
	
	public void assembleHalfMaps(UniqueGameIdentifier gameID) {
		
		GameData game = this.games.get(gameID.getUniqueGameID());
		game.getFullMap().assembleFullMap(game, game.getPlayers(), game.getPlayers().get(0).getHalfMap(), game.getPlayers().get(1).getHalfMap());
		this.games.get(gameID.getUniqueGameID()).setFullMap(game.getFullMap());

	}

	public GameState requestGameState(UniquePlayerIdentifier playerID, UniqueGameIdentifier gameID, NetworkConverter networkConverter) {
		
		GameData game = this.games.get(gameID.getUniqueGameID());
		
		Set<PlayerState> players = new HashSet<>();
		
		Optional<FullMap> map = Optional.empty();
		
		List<Player> registeredPlayers = game.getPlayers();
		
		if (registeredPlayers.size() == 1) {
		
			Player player = registeredPlayers.get(0);
			
			players.add(networkConverter.convertPlayerTo(this.games.get(gameID.getUniqueGameID()), player, false));
		
			return new GameState(map, players, game.getGameStateId());
			
		} else if (registeredPlayers.size() == 2) {
			
			Player player1 = game.getPlayerWithId(playerID.getUniquePlayerID());
			Player player2 = game.getTheOtherPlayer(playerID.getUniquePlayerID());
			
			players.add(networkConverter.convertPlayerTo(this.games.get(gameID.getUniqueGameID()), player1, false));
			players.add(networkConverter.convertPlayerTo(this.games.get(gameID.getUniqueGameID()), player2, true));
			
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
		
		player.processMove(game, gameID, move, networkConverter);
		player.updateTreasureStatus(game);
		player.updateEnemyFortStatus(game);
		player.updateMountainViewStatus(game);

	}
}
