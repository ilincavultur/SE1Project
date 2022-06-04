package server.models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class GameData {

	private String gameId;
	private List<Player> players = new ArrayList<Player>();
	private InternalFullMap fullMap = new InternalFullMap();
	private Instant gameCreationTime;
	private String gameStateId;
	private int idxPlayersTurn;
	private String winnerId;
	
	public GameData() {
		super();
		this.gameId = null;
		this.players = new ArrayList<Player>();
		this.fullMap = new InternalFullMap();
		this.gameCreationTime = Instant.now();
		this.gameStateId = UUID.randomUUID().toString();
		Random randomNo = new Random();
		this.idxPlayersTurn = randomNo.nextInt(2);
		this.winnerId = null;
	}
	
	public GameData(String gameId, List<Player> players, InternalFullMap fullMap) {
		super();
		this.gameId = gameId;
		this.players = players;
		this.fullMap = fullMap;
		this.gameCreationTime = Instant.now();
		this.gameStateId = UUID.randomUUID().toString();
		Random randomNo = new Random();
		this.idxPlayersTurn = randomNo.nextInt(2);
		this.winnerId = null;
	}
	public String getGameId() {
		return gameId;
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
	public InternalFullMap getFullMap() {
		return fullMap;
	}
	public void setFullMap(InternalFullMap fullMap) {
		this.fullMap = fullMap;
	}
	
	
	
	public String getWinnerId() {
		return winnerId;
	}

	public void setWinnerId(String winnerId) {
		this.winnerId = winnerId;
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

	public void setGameCreationTime(Instant gameCreationTime) {
		this.gameCreationTime = gameCreationTime;
	}
	
	public boolean myTurn (String playerId) {
		if (players.get(0).getPlayerId().equals(playerId)) {
			if (idxPlayersTurn == 0) {
				return true;
			}
		} else if (players.get(1).getPlayerId().equals(playerId)) {
			if (idxPlayersTurn == 1) {
				return true;
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
	}

	public Player getPlayerWithId(String playerId) {
		Player toRet = new Player();
		
		for (Player player: players) {
			if (player.getPlayerId().equals(playerId)) {
				toRet = player;
				break;
			}
		}
		return toRet;
	}
	
}