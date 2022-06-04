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

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import MessagesBase.MessagesFromServer.FullMap;
import MessagesBase.MessagesFromServer.GameState;
import MessagesBase.MessagesFromServer.PlayerState;
import server.models.GameData;
import server.models.InternalHalfMap;
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
		List<Player> players = this.games.get(gameId.getUniqueGameID()).getPlayers();
		players.add(newPlayer);
		this.games.get(gameId.getUniqueGameID()).setPlayers(players);
	}
	
	// validate done in servernedpoints

	// translate
	
	public void receiveHalfMap(InternalHalfMap halfMap, String playerId, String gameId) {
		List<Player> players = this.games.get(gameId).getPlayers();
		for (Player player: players) {
			if (player.getPlayerId().equals(playerId)) {
				player.setHalfMap(halfMap);
			}
		}
		
		// save
	}
	
	public boolean myTurn (UniquePlayerIdentifier playerID, UniqueGameIdentifier gameID) {
		
		GameData game = this.games.get(gameID.getUniqueGameID());
		
		return game.myTurn(playerID.getUniquePlayerID());
	}
	
	public void swapPlayerOnTurn(UniqueGameIdentifier gameID) {
		GameData game = this.games.get(gameID.getUniqueGameID());
		game.swapPlayerOnTurn();
	}
	
	//validate done in servernedpoints

	// translate
	
	// nush daca iti trebuie pt ca daca not noth maps present inseamna ca full map e null
	public boolean bothHalfMapsPresent(UniqueGameIdentifier gameID) {
	
		for (Player pl : this.games.get(gameID.getUniqueGameID()).getPlayers()) {
			if (pl.getHalfMap() == null) {
				return false;
			}
		}
		
		return true;
	}
	
	public GameState requestGameState(UniquePlayerIdentifier playerID, UniqueGameIdentifier gameID, NetworkConverter networkConverter) {
		
		GameData game = this.games.get(gameID.getUniqueGameID());
		Player player = this.games.get(gameID.getUniqueGameID()).getPlayerWithId(playerID.getUniquePlayerID());
		Player enemy = null;
		
		Set<PlayerState> players = new HashSet<>();
		for (Player pl : this.games.get(gameID.getUniqueGameID()).getPlayers()) {
			if (!pl.getPlayerId().equals(playerID.getUniquePlayerID())) {
				enemy = pl;
				players.add(networkConverter.convertPlayerFrom(this.games.get(gameID.getUniqueGameID()), player, true));
				
			} else {
				players.add(networkConverter.convertPlayerFrom(this.games.get(gameID.getUniqueGameID()), player, false));
			}
			
		}
		
		Optional<FullMap> map = networkConverter.convertServerFullMapTo(player, enemy, game.getFullMap(), game);
		
		
		return new GameState(map, players, game.getGameStateId());
	}
	
	//validate done in servernedpoints
	public void receiveMove(PlayerMove move) {
		
		//process
		
	}
}
