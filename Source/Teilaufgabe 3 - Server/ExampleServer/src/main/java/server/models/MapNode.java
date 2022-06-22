package server.models;
import server.enums.*;

public class MapNode {

	private Coordinates position = new Coordinates();
	private FortState fortState = FortState.UNKNOWNIFFORT;
	private PlayerPositionState playerPosState = PlayerPositionState.NOPLAYER;
	private TreasureState treasureState = TreasureState.UNKNOWNIFTREASURE;
	private MapFieldType fieldType = MapFieldType.GRASS;
	
	public MapNode() {
		super();
	}

	public MapFieldType getFieldType() {
		return fieldType;
	}
	
	public void setFieldType(MapFieldType fieldType) {
		this.fieldType = fieldType;
	}
	
	public Coordinates getPosition() {
		return position;
	}

	public void setPosition(Coordinates position) {
		this.position = position;
	}

	public TreasureState getTreasureState() {
		return treasureState;
	}

	public void setTreasureState(TreasureState treasureState) {
		this.treasureState = treasureState;
	}

}
