package client.models.gameData;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.Objects;

import client.models.gameData.enums.ClientPlayerState;
import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.models.mapData.enums.FortState;
import client.models.mapData.enums.PlayerPositionState;

public class GameStateData {
	
	private String playerId;
	private String gameStateId;
	private ClientPlayerState playerState;
	private Boolean hasCollectedTreasure;
	private Coordinates playerPosition;
	private ClientMap fullMap;
	private final PropertyChangeSupport notifyChanges = new PropertyChangeSupport(this);
	
	public void registerInterestedView(PropertyChangeListener listener) {
		notifyChanges.addPropertyChangeListener(listener);
	}
	
	
	public GameStateData() {
		super();
	}

	public GameStateData(GameStateData obj) {
		this.gameStateId = obj.getGameStateId();
		this.hasCollectedTreasure = obj.hasCollectedTreasure;
		this.playerState = obj.playerState;
		this.playerId = obj.playerId;
		if(obj.fullMap != null) {
			this.fullMap = obj.fullMap;		
		}
	
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
		// String oldGameStateId = this.gameStateId;
		this.gameStateId = gameStateId;
		
		//notifyChanges.firePropertyChange("", oldGameStateId, gameStateid);
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


	public ClientMap getFullMap() {
		return fullMap;
	}

	public void setFullMap(ClientMap fullMap) {
		this.fullMap = fullMap;
	}
	
	public MapField getMyCurrentPosition() {

		if (fullMap !=null) {
			for( Map.Entry<Coordinates, MapField> mapEntry : fullMap.getFields().entrySet() ) {
				if (mapEntry.getValue().getPlayerPositionState() == PlayerPositionState.MYPLAYER || mapEntry.getValue().getPlayerPositionState() == PlayerPositionState.BOTH) {
					return mapEntry.getValue();
				}
			}
		}
		
		return null;
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
