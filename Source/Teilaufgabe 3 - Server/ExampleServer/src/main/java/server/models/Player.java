package server.models;

public class Player {

	String playerId;
	InternalHalfMap halfMap;
	Coordinates currPos;
	
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public InternalHalfMap getHalfMap() {
		return halfMap;
	}
	public void setHalfMap(InternalHalfMap halfMap) {
		this.halfMap = halfMap;
	}
	public Coordinates getCurrPos() {
		return currPos;
	}
	public void setCurrPos(Coordinates currPos) {
		this.currPos = currPos;
	}
	
	
}
