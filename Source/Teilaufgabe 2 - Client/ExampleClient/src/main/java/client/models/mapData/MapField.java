package client.models.mapData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import client.models.mapData.enums.FortState;
import client.models.mapData.enums.MapFieldType;
import client.models.mapData.enums.PlayerPositionState;
import client.models.mapData.enums.TreasureState;

public class MapField {
	
	private Coordinates position;
	private MapFieldType type;
	private PlayerPositionState playerPositionState;
	private TreasureState treasureState;
	private FortState fortState;
	private List<Coordinates> shortestPath;
	
	
	public MapField() {
		super();
	}
	
	public MapField createMapField(Coordinates pos) {
		
		MapField newMapField = new MapField();
		
		newMapField.position = pos;
		
		newMapField.fortState = FortState.UNKNOWNIFFORT;
		
		newMapField.playerPositionState = PlayerPositionState.NOPLAYER;
		
		newMapField.treasureState = TreasureState.UNKNOWNIFTREASURE;
		
		newMapField.type = MapFieldType.GRASS;

		newMapField.shortestPath = new ArrayList<Coordinates>();
		
		return newMapField;
		
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

	public List<Coordinates> getShortestPath() {
		return shortestPath;
	}

	public void setShortestPath(List<Coordinates> shortestPath) {
		this.shortestPath = shortestPath;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fortState, playerPositionState, position, treasureState, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapField other = (MapField) obj;
		return fortState == other.fortState && playerPositionState == other.playerPositionState
				&& Objects.equals(position, other.position) && treasureState == other.treasureState
				&& type == other.type;
	}
	

	
	
	
	
}
