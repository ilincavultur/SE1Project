package client.models.mapData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		//this.position = pos;
		newMapField.fortState = FortState.UNKNOWNIFFORT;
		//this.fortState = FortState.UNKNOWNIFFORT;
		newMapField.playerPositionState = PlayerPositionState.NOPLAYER;
		//this.playerPositionState = PlayerPositionState.NOPLAYER;
		newMapField.treasureState = TreasureState.UNKNOWNIFTREASURE;
		//this.treasureState = TreasureState.UNKNOWNIFTREASURE;
		newMapField.type = MapFieldType.GRASS;

		newMapField.shortestPath = new ArrayList<Coordinates>();
		
		return newMapField;
		
	}
	/*

	public MapField createMapField(Coordinates pos, int fieldType) {
		
		MapField newMapField = new MapField();
		newMapField.position = pos;
		//this.position = pos;
		newMapField.fortState = FortState.UNKNOWNIFFORT;
		//this.fortState = FortState.UNKNOWNIFFORT;
		newMapField.playerPositionState = PlayerPositionState.NOPLAYER;
		//this.playerPositionState = PlayerPositionState.NOPLAYER;
		newMapField.treasureState = TreasureState.UNKNOWNIFTREASURE;
		//this.treasureState = TreasureState.UNKNOWNIFTREASURE;
		if(fieldType == 0) {
			newMapField.type = MapFieldType.GRASS;
			newMapField.moves = 1;
		}
		if(fieldType == 1) {
			newMapField.type = MapFieldType.MOUNTAIN;
			newMapField.moves = 2;
		}
		if(fieldType == 2) {
			newMapField.type = MapFieldType.WATER;
			newMapField.moves = 1;
		}
		newMapField.shortestPath = new ArrayList<Coordinates>();
		
		
		return newMapField;
		
	}*/
/*
	public MapField(Coordinates position, MapFieldType type, PlayerPositionState playerPositionState,
			TreasureState treasureState, FortState fortState) {
		super();
		this.position = position;
		this.type = type;
		this.playerPositionState = playerPositionState;
		this.treasureState = treasureState;
		this.fortState = fortState;
		if(type == MapFieldType.GRASS) {
			this.moves = 1;
		}
		if(type == MapFieldType.MOUNTAIN) {
			this.moves = 2;
		}
		if(type == MapFieldType.WATER) {
			this.moves = 1;
		}
		this.shortestPath = new ArrayList<Coordinates>();
	}*/

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
