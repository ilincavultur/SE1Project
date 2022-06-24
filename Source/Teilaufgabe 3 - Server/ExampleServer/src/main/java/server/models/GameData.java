package server.models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import MessagesBase.UniqueGameIdentifier;
import server.controllers.FullMapHandler;
import server.enums.MoveCommand;

public class GameData {

	private String gameId;
	private List<Player> players = new ArrayList<Player>();
	private FullMapHandler fullMap = new FullMapHandler();
	private Instant gameCreationTime;
	private String gameStateId;
	private int idxPlayersTurn;
	private String winnerId;
	private int roundNo;
	private boolean changed;
	
	public GameData(String gameId) {
		super();
		this.gameId = gameId;
		this.players = new ArrayList<Player>();
		this.fullMap = new FullMapHandler();
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
	public List<Player> getPlayers() {
		return players;
	}
	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	public FullMapHandler getFullMap() {
		return fullMap;
	}
	public void setFullMap(FullMapHandler fullMap) {
		this.fullMap = fullMap;
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
	
	/*
	 * 	If not all players registered it means it is not player's turn to send map
	 *  Returns true if player's turn
	 */
	public boolean myTurn(String playerId) {

		if (this.players.size() == 1) {
			return false;
		} else if (this.players.size() == 2) {
			if (players.get(0).getPlayerId().equals(playerId)) {
				if (idxPlayersTurn == 0) {
					return true;
				}
			} else if (players.get(1).getPlayerId().equals(playerId)) {
				if (idxPlayersTurn == 1) {
					return true;
				}
			}
		}
		
		return false;
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
		Player toReturn = new Player();
		
		for (Player player: players) {
			if (player.getPlayerId().equals(playerId)) {
				toReturn = player;
				break;
			}
		}
		return toReturn;
	}
	
	public void updateGameStateId() {
		String newGameStateId = UUID.randomUUID().toString();
		this.setGameStateId(newGameStateId);
	}
	
	/*
	 *  returns the player whose id is not equal to the parameter
	 */
	public Player getTheOtherPlayer(String playerId) {
		Player toReturn = new Player();
		
		for (Player player: players) {
			if (!player.getPlayerId().equals(playerId)) {
				toReturn = player;
				break;
			}
		}
		return toReturn;
	}
	
}
