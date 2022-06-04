package server.models;

import java.util.ArrayList;
import java.util.List;

public class GameState {

	private String gameId;
	private List<Player> players = new ArrayList<Player>();
	private InternalFullMap fullMap = new InternalFullMap();
	
	public GameState() {
		super();
		this.gameId = null;
		this.players = new ArrayList<Player>();;
		this.fullMap = new InternalFullMap();;
	}
	
	public GameState(String gameId, List<Player> players, InternalFullMap fullMap) {
		super();
		this.gameId = gameId;
		this.players = players;
		this.fullMap = fullMap;
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
