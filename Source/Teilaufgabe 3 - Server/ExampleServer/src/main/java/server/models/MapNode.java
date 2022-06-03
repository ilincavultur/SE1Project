package server.models;
import server.enums.*;

public class MapNode {

	private Coordinates position = new Coordinates();
	private FortState fortState = FortState.UNKNOWNIFFORT;
	private PlayerPositionState playerPosState = PlayerPositionState.NOPLAYER;
	private TreasureState treasureState = TreasureState.UNKNOWNIFTREASURE;
	private MapFieldType fieldType = MapFieldType.GRASS;
	
	
	
	public MapFieldType getFieldType() {
		return fieldType;
	}



	public void setFieldType(MapFieldType fieldType) {
		this.fieldType = fieldType;
	}



	public MapNode() {
		super();
	}



	public MapNode(Coordinates position, FortState fortState, PlayerPositionState playerPosState,
			TreasureState treasureState, MapFieldType fieldType) {
		super();
		this.position = position;
		this.fortState = fortState;
		this.playerPosState = playerPosState;
		this.treasureState = treasureState;
		this.fieldType = fieldType;
	}



	public Coordinates getPosition() {
		return position;
	}



	public void setPosition(Coordinates position) {
		this.position = position;
	}



	public FortState getFortState() {
		return fortState;
	}



	public void setFortState(FortState fortState) {
		this.fortState = fortState;
	}



	public PlayerPositionState getPlayerPosState() {
		return playerPosState;
	}



	public void setPlayerPosState(PlayerPositionState playerPosState) {
		this.playerPosState = playerPosState;
	}



	public TreasureState getTreasureState() {
		return treasureState;
	}



	public void setTreasureState(TreasureState treasureState) {
		this.treasureState = treasureState;
	}
	
	
	
	public boolean isFortPresent() {
		return this.fortState == FortState.MYFORT;
	}
	
	
}
