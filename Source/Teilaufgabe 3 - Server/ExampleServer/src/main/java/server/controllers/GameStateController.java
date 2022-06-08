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
	private static int maximumGamesNumber = 999;
	private static final Logger logger = LoggerFactory.getLogger(GameStateController.class);
	private boolean first = true;
	
	
	List<IRuleValidation> rules = new ArrayList<IRuleValidation>();	

	public GameStateController() {
		super();
	
		rules.add(new BothPlayersRegisteredRule());
		rules.add(new DontMoveIntoWaterRule());
		rules.add(new DontMoveOutsideMapRule());
		rules.add(new FieldsCoordinatesRule());
		rules.add(new FortRule());
		rules.add(new GameIdRule());
		rules.add(new HalfMapSizeRule());
		rules.add(new MaxNoOfPlayersReachedRule());
		rules.add(new MyTurnRule());
		rules.add(new NoIslandsRule());
		rules.add(new PlayerIdRule());
		rules.add(new TerrainsNumberRule());
		rules.add(new WaterOnEdgesRule());
		
	}

	public Map<String, GameData> getGames() {
		return games;
	}

	public void setGames(Map<String, GameData> games) {
		this.games = games;
	}

	// SCHIMBA
	public UniqueGameIdentifier createUniqueGameId() {
		
		String toReturn = null;
		
		Random randomNo = new Random();
		String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int no = alphabet.length();
		
		while (toReturn == null || games.containsKey(toReturn)) {
			toReturn = "";
		
			for (int i=0; i<5; i++) {
				int nextCharacter = randomNo.nextInt(no);
				toReturn += alphabet.charAt(nextCharacter);
			}
			
		}
		
		UniqueGameIdentifier toRet = new UniqueGameIdentifier(toReturn);
		
		return toRet;
	}
	
	public UniquePlayerIdentifier createUniquePlayerId() {
		
		UniquePlayerIdentifier toRet = new UniquePlayerIdentifier(UUID.randomUUID().toString());
		
		return toRet;
	}
	
	public String getOldestGameId() {
		String toRet = "";
		Duration longestDur = Duration.ZERO;
		for (Entry<String, GameData> mapEntry : this.games.entrySet()) {
			Duration entryDur = Duration.between(mapEntry.getValue().getGameCreationTime(), Instant.now());
			if (entryDur.compareTo(longestDur) >= 0) {
				longestDur = entryDur;
				toRet = mapEntry.getKey();
			}
		}
		
		return toRet;
	}
	
	public void removeGame(String gameId) {
		this.games.remove(gameId);
	}

	public void updateGameStateId(UniqueGameIdentifier gameID) {
		String newGameStateId = UUID.randomUUID().toString();
		
		this.games.get(gameID.getUniqueGameID()).setGameStateId(newGameStateId);
	}
	
	public void createNewGame(UniqueGameIdentifier gameId) {
		
		if (this.games.size() >= maximumGamesNumber) {
			
			String oldestGameId = getOldestGameId();
			
			removeGame(oldestGameId);
			
		}
		
		GameData newGame = new GameData();
		newGame.setGameId(gameId.getUniqueGameID());
		this.games.put(gameId.getUniqueGameID(), newGame);
	}
	
	// validation already made in serverendpoints
	public void registerPlayer (UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId, PlayerRegistration playerReg) {
		Player newPlayer = new Player();
		newPlayer.setPlayerId(playerId.getUniquePlayerID());
		newPlayer.setPlayerReg(playerReg);
		newPlayer.setHalfMap(null);
		List<Player> players = this.games.get(gameId.getUniqueGameID()).getPlayers();
		players.add(newPlayer);
		this.games.get(gameId.getUniqueGameID()).setPlayers(players);
	}
	
	public Coordinates placeTreasure(InternalHalfMap halfMap) {
		
		//GameData game = this.games.get(gameId);
		//Player player = game.getPlayerWithId(playerId);
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
	
	// validate done in servernedpoints
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
	
	public boolean myTurn (UniquePlayerIdentifier playerID, UniqueGameIdentifier gameID) {
		
		GameData game = this.games.get(gameID.getUniqueGameID());
		
		return game.myTurn(playerID.getUniquePlayerID());
	}
	
	public void swapPlayerOnTurn(UniqueGameIdentifier gameID) {
		GameData game = this.games.get(gameID.getUniqueGameID());
		game.swapPlayerOnTurn();
	}
	
	public boolean bothPlayersRegistered(UniqueGameIdentifier gameID) {
		
		if (this.games.get(gameID.getUniqueGameID()).getPlayers().size() != 2) {
			return false;
		}
		
		return true;
	}

	public boolean bothHalfMapsPresent(UniqueGameIdentifier gameID) {
	
		for (Player pl : this.games.get(gameID.getUniqueGameID()).getPlayers()) {
			if (pl.getHalfMap() == null) {
				return false;
			}
		}
		
		return true;
	}
	
	public void assembleHalfMaps(UniqueGameIdentifier gameID) {
		GameData game = this.games.get(gameID.getUniqueGameID());
		game.getFullMap().assembleFullMap(game, game.getPlayers(), game.getPlayers().get(0).getHalfMap(), game.getPlayers().get(1).getHalfMap());
		this.games.get(gameID.getUniqueGameID()).setFullMap(game.getFullMap());
		
		//game.getPlayers().get(0).setTreasurePos(game.getPlayers().get(0).getHalfMap().getTreasurePos());
		//game.getPlayers().get(1).setTreasurePos(game.getPlayers().get(1).getHalfMap().getTreasurePos());
		
	}
	
	private int getMoves(MapNode field) {
		if (field.getFieldType() == MapFieldType.MOUNTAIN) {
			return 2;
		} 
		return 1;
	}
	
	private int getPathWeight(Player player, GameData game, Coordinates currentField, Coordinates nextField) {

		int firstFieldMoves = this.getMoves(game.getFullMap().getFields().get(currentField)); 
		int secondFieldMoves = this.getMoves(game.getFullMap().getFields().get(nextField)); 
 		
		return firstFieldMoves + secondFieldMoves;
		
	}
	
	private Coordinates getTargetCoordinatesFromMove(GameData game, Coordinates pos, PlayerMove move) {
		Coordinates toRet = new Coordinates();
		Map<Coordinates, MapNode> fields = game.getFullMap().getFields();
		
		if (move.getMove() == EMove.Up) {
			Coordinates dir = pos.getUpNeighbour(fields);
			return dir;
		}
		
		if (move.getMove() == EMove.Down) {
			Coordinates dir = pos.getDownNeighbour(fields);
			return dir;
		}
		
		if (move.getMove() == EMove.Left) {
			Coordinates dir = pos.getLeftNeighbour(fields);
			return dir;
		}
		
		if (move.getMove() == EMove.Right) {
			Coordinates dir = pos.getRightNeighbour(fields);
			return dir;
		}
		
		return toRet;
	}
	
	
	public GameState requestGameState(UniquePlayerIdentifier playerID, UniqueGameIdentifier gameID, NetworkConverter networkConverter) {
		
		GameData game = this.games.get(gameID.getUniqueGameID());
		
		Set<PlayerState> players = new HashSet<>();
		
		Optional<FullMap> map = Optional.empty();
		
		List<Player> registeredPlayers = game.getPlayers();
		
		if (registeredPlayers.size() == 1) {
			
			Player player = registeredPlayers.get(0);
			
			players.add(networkConverter.convertPlayerTo(this.games.get(gameID.getUniqueGameID()), player, false));
			
			if (player.getHalfMap() != null) {
				map = networkConverter.convertIHalfMapToNetworkFullMap(player, game);	
			}
			
			
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
	
	
	
	//validate done in servernedpoints
	public void receiveMove(UniqueGameIdentifier gameID, PlayerMove move, NetworkConverter networkConverter) {
		GameData game = this.games.get(gameID.getUniqueGameID());
		
		Player player = game.getPlayerWithId(move.getUniquePlayerID());
		player.processMove(game, gameID, move, networkConverter);
		//logger.info("player " + player.getPlayerId() + " has " + player.noOfMyTreasures() + " my treasures");
		player.updateTreasureStatus(game);
		player.updateEnemyFortStatus(game);
		
		player.updateMountainViewStatus(game);
		
		
		//logger.info("curr pos " + player.getCurrPos().getX() + " " +  player.getCurrPos().getY());
		
		
		
	}
}
