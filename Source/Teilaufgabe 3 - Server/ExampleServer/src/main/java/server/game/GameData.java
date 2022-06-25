package server.game;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
import server.enums.MoveCommand;
import server.map.FullMapHandler;
import server.network.NetworkConverter;
import server.player.Player;
import server.player.PlayerController;

public class GameData {

	private String gameId;
	private PlayerController playerController;
	private FullMapHandler fullMapHandler;
	private Instant gameCreationTime;
	private String gameStateId;
	private int idxPlayersTurn;
	private String winnerId;
	private int roundNo;
	private boolean changed;
	
	public GameData(String gameId) {
		super();
		this.gameId = gameId;
		this.playerController = new PlayerController();
		this.fullMapHandler = new FullMapHandler();
		this.changed = false;
		this.gameCreationTime = Instant.now();
		this.gameStateId = UUID.randomUUID().toString();
		Random randomNo = new Random();
		this.idxPlayersTurn = randomNo.nextInt(2);
		this.winnerId = "";
		this.roundNo = 0;
		pickFirstPlayerToSendMap();
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public FullMapHandler getFullMap() {
		return fullMapHandler;
	}
	public void setFullMap(FullMapHandler fullMapHandler) {
		this.fullMapHandler = fullMapHandler;
	}

	public int getRoundNo() {
		return roundNo;
	}

	public String getWinnerId() {
		return winnerId;
	}

	public String getGameStateId() {
		return gameStateId;
	}

	public void setGameStateId(String gameStateId) {
		this.gameStateId = gameStateId;
	}

	public Instant getGameCreationTime() {
		return gameCreationTime;
	}

	public void setWinner(String loserId) {
		Player winner = getTheOtherPlayer(loserId);
		this.winnerId = winner.getPlayerId();
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	public int getIdxPlayersTurn() {
		return idxPlayersTurn;
	}

	public PlayerController getPlayerController() {
		return playerController;
	}

	public void pickFirstPlayerToSendMap() {
		Random randomNo = new Random();
		int no = randomNo.nextInt(2);
		if (no == 0) {
			this.idxPlayersTurn = 0;
			return;
		}
		this.idxPlayersTurn = 1;
		return;
	}
	
	public void registerPlayer (UniquePlayerIdentifier playerId, PlayerRegistration playerReg) {
		playerController.registerPlayer(playerId, playerReg);
	}
	
	public boolean bothHalfMapsPresent(UniqueGameIdentifier gameID) {	
		return playerController.bothHalfMapsPresent();
	}
	
	/*
	 * 	If not all players registered it means it is not player's turn to send map
	 *  Returns true if player's turn
	 */
	public boolean myTurn(String playerId) {
		return playerController.myTurn(playerId, idxPlayersTurn);
	}
	
	public void swapPlayerOnTurn() {
		if (idxPlayersTurn == 0) {
			idxPlayersTurn = 1;
		} else {
			idxPlayersTurn = 0;
		}
		roundNo += 1;
	}

	public Player getPlayerWithId(String playerId) {
		return playerController.getPlayerWithId(playerId);
	}
	
	public void updateGameStateId() {
		this.gameStateId = UUID.randomUUID().toString();
	}
	
	/*
	 *  returns the player whose id is not equal to the parameter
	 */
	public Player getTheOtherPlayer(String playerId) {
		return playerController.getTheOtherPlayer(playerId);
	}
	
	public GameState requestGameState(UniquePlayerIdentifier playerID, UniqueGameIdentifier gameID, NetworkConverter networkConverter) {
		
		Set<PlayerState> players = new HashSet<>();
		Optional<FullMap> map = Optional.empty();
		List<Player> registeredPlayers = playerController.getPlayers();
		String playerId = playerID.getUniquePlayerID();
		
		if (registeredPlayers.size() == 1) {
			
			Player player = registeredPlayers.get(0);
			players.add(networkConverter.convertPlayerTo(this, player, false));
			
			return new GameState(map, players, this.gameStateId);
			
		} else if (registeredPlayers.size() == 2) {
			
			Player player1 = playerController.getPlayerWithId(playerId);
			Player player2 = playerController.getTheOtherPlayer(playerId);
			
			players.add(networkConverter.convertPlayerTo(this, player1, false));
			players.add(networkConverter.convertPlayerTo(this, player2, true));
			
			if (bothHalfMapsPresent(gameID)) {
				fullMapHandler.assembleFullMap(this, playerController.getPlayers(), playerController.getPlayers().get(0).getHalfMap(), playerController.getPlayers().get(1).getHalfMap());
				map = networkConverter.convertServerFullMapTo(playerID, this);
			} else {
				if (player1.isPlayersHalfMapPresent()) {
					map = networkConverter.convertIHalfMapToNetworkFullMap(player1, this);
					
				} else if (player2.isPlayersHalfMapPresent()) {
					map = networkConverter.convertIHalfMapToNetworkFullMap(player2, this);
				}	
			}		
		}
		
		return new GameState(map, players, this.gameStateId);
	}
	
	public void receiveMove(UniqueGameIdentifier gameID, PlayerMove move, NetworkConverter networkConverter) {
		playerController.movePlayer(this, move, networkConverter);
	}
	
}
