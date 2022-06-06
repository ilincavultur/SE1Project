package server.models;

import MessagesBase.MessagesFromClient.PlayerRegistration;

public class Player {

	String playerId;
	InternalHalfMap halfMap;
	Coordinates currPos;
	boolean hasCollectedTreasure;
	PlayerRegistration playerReg;
	
	
	public Player() {
		super();
		Coordinates currPos = new Coordinates(0,0);
		this.currPos = currPos;
		// TODO Auto-generated constructor stub
	}
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
	public boolean isHasCollectedTreasure() {
		return hasCollectedTreasure;
	}
	public void setHasCollectedTreasure(boolean hasCollectedTreasure) {
		this.hasCollectedTreasure = hasCollectedTreasure;
	}
	public PlayerRegistration getPlayerReg() {
		return playerReg;
	}
	public void setPlayerReg(PlayerRegistration playerReg) {
		this.playerReg = playerReg;
	}
	

	public boolean isPlayersHalfMapPresent() {
		return this.getHalfMap() != null;
	}
	
	
}
