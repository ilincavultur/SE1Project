package client.models.gameData;

import java.util.Objects;

import client.models.gameData.enums.ClientPlayerState;
import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;

public class GameStateData {
	String playerId;
	String gameStateId;
	ClientPlayerState playerState;
	Boolean hasCollectedTreasure;
	Coordinates playerPosition;
	ClientMap map;
	
	
	
	public GameStateData() {
		super();
	}

	public GameStateData(GameStateData obj) {
		this.gameStateId = obj.getGameStateId();
		this.hasCollectedTreasure = obj.hasCollectedTreasure;
		this.playerState = obj.playerState;
		this.playerId = obj.playerId;
	}


	public GameStateData(String playerId, String gameStateId, ClientPlayerState playerState, Boolean hasCollectedTreasure,
			Coordinates playerPosition, ClientMap map) {
		super();
		this.playerId = playerId;
		this.gameStateId = gameStateId;
		this.playerState = playerState;
		this.hasCollectedTreasure = hasCollectedTreasure;
		this.playerPosition = playerPosition;
		this.map = map;
	}



	public String getPlayerId() {
		return playerId;
	}



	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}



	public String getGameStateId() {
		return gameStateId;
	}



	public void setGameStateId(String gameStateId) {
		this.gameStateId = gameStateId;
	}



	public ClientPlayerState getPlayerState() {
		return playerState;
	}



	public void setPlayerState(ClientPlayerState playerState) {
		this.playerState = playerState;
	}



	public Boolean getHasCollectedTreasure() {
		return hasCollectedTreasure;
	}



	public void setHasCollectedTreasure(Boolean hasCollectedTreasure) {
		this.hasCollectedTreasure = hasCollectedTreasure;
	}



	public Coordinates getPlayerPosition() {
		return playerPosition;
	}



	public void setPlayerPosition(Coordinates playerPosition) {
		this.playerPosition = playerPosition;
	}



	public ClientMap getMap() {
		return map;
	}



	public void setMap(ClientMap map) {
		this.map = map;
	}



	@Override
	public int hashCode() {
		return Objects.hash(playerId);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameStateData other = (GameStateData) obj;
		return Objects.equals(playerId, other.playerId);
	}
	
	
	
	
	
}
