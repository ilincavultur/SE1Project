package server.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import server.models.GameState;
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

	private Map<String, GameState> games = new HashMap<String, GameState>();
	
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

	public Map<String, GameState> getGames() {
		return games;
	}

	public void setGames(Map<String, GameState> games) {
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
	
	public void createNewGame(UniqueGameIdentifier gameId) {
		GameState newGame = new GameState();
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
	
	public void receiveHalfMap(InternalHalfMap halfMap) {
		
		
		// save
	}
	
	//validate done in servernedpoints

	// translate
	
	public void requestGameState() {
	
		
		// save
	}
	
	//validate done in servernedpoints
	public void receiveMove(PlayerMove move) {
		
		//process
		
	}
}
