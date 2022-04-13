package client.models.mapData;

import client.models.mapData.enums.FortState;
import client.models.mapData.enums.MapFieldType;
import client.models.mapData.enums.PlayerPositionState;
import client.models.mapData.enums.TreasureState;

public class MapField {
	
	Coordinates position;
	MapFieldType type;
	PlayerPositionState playerPositionState;
	TreasureState treasureState;
	FortState fortState;
	
	public MapField(Coordinates position, MapFieldType type, PlayerPositionState playerPositionState,
			TreasureState treasureState, FortState fortState) {
		super();
		this.position = position;
		this.type = type;
		this.playerPositionState = playerPositionState;
		this.treasureState = treasureState;
		this.fortState = fortState;
	}

	public Coordinates getPosition() {
		return position;
	}

	public void setPosition(Coordinates position) {
		this.position = position;
	}

	public MapFieldType getType() {
		return type;
	}

	public void setType(MapFieldType type) {
		this.type = type;
	}

	public PlayerPositionState getPlayerPositionState() {
		return playerPositionState;
	}

	public void setPlayerPositionState(PlayerPositionState playerPositionState) {
		this.playerPositionState = playerPositionState;
	}

	public TreasureState getTreasureState() {
		return treasureState;
	}

	public void setTreasureState(TreasureState treasureState) {
		this.treasureState = treasureState;
	}

	public FortState getFortState() {
		return fortState;
	}

	public void setFortState(FortState fortState) {
		this.fortState = fortState;
	}
	
	
	
	
	
}
