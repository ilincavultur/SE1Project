package server.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.MessagesFromClient.EMove;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import server.controllers.FullMapHandler;
import server.controllers.GameStateController;
import server.enums.FortState;
import server.enums.MapFieldType;
import server.enums.MoveCommand;
import server.enums.TreasureState;
import server.network.NetworkConverter;

public class Player {

	private String playerId;
	private Optional<InternalHalfMap> halfMap;
	private Coordinates currPos;
	boolean hasCollectedTreasure;
	private PlayerRegistration playerReg;
	private boolean showEnemyFort;
	private boolean showTreasure;
	private MoveCommand currentDirection;
	private int currentNoOfStepsToTake;
	private Coordinates treasurePos;
	private Coordinates fortPos;
	
	private static final Logger logger = LoggerFactory.getLogger(Player.class);
	
	public Player(String playerId, PlayerRegistration playerReg) {
		super();
		this.playerId = playerId;
		this.playerReg = playerReg;
		Coordinates currPos = new Coordinates(0,0);
		this.halfMap = Optional.empty();
		this.currPos = currPos;
		this.showEnemyFort = false;
		this.currentNoOfStepsToTake = 0;
		this.hasCollectedTreasure = false;
		this.showTreasure = false;
		this.treasurePos = new Coordinates(0,0);
		this.fortPos = new Coordinates(0,0);
	}
	public Player() {
		super();
		this.playerId = "";
		Coordinates currPos = new Coordinates(0,0);
		this.halfMap = Optional.empty();
		this.currPos = currPos;
		this.showEnemyFort = false;
		this.currentNoOfStepsToTake = 0;
		this.hasCollectedTreasure = false;
		this.showTreasure = false;
		this.treasurePos = new Coordinates(0,0);
		this.fortPos = new Coordinates(0,0);
	}
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public Optional<InternalHalfMap> getHalfMap() {
		return halfMap;
	}
	public void setHalfMap(InternalHalfMap halfMap) {
		this.halfMap = Optional.of(halfMap);
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
	public boolean isShowEnemyFort() {
		return showEnemyFort;
	}
	public void setShowEnemyFort(boolean showEnemyFort) {
		this.showEnemyFort = showEnemyFort;
	}
	public boolean isPlayersHalfMapPresent() {
		return this.halfMap.isPresent();
	}
	public Coordinates getFortPos() {
		return fortPos;
	}
	public void setFortPos(Coordinates fortPos) {
		this.fortPos = fortPos;
	}
	public Coordinates getTreasurePos() {
		return treasurePos;
	}
	public void setTreasurePos(Coordinates treasurePos) {
		this.treasurePos = treasurePos;
	}
	public boolean isShowTreasure() {
		return showTreasure;
	}
	public void setShowTreasure(boolean showTreasure) {
		this.showTreasure = showTreasure;
	}
	
	public void receiveHalfMap(InternalHalfMap halfMap, String gameId) {
		this.setCurrPos(halfMap.getFortPos());
		Coordinates treasurePosition = halfMap.placeTreasure();
		halfMap.getFields().get(treasurePosition).setTreasureState(TreasureState.MYTREASURE);
		this.setHalfMap(halfMap);
	}
	
	/*
	 *  Updates player position if player has finished move
	 *  Decreases number of steps to take if player has not finished move
	 */
	public void processMove(FullMapHandler fullMap, PlayerMove move, NetworkConverter networkConverter) {
		
		MoveCommand newMove = networkConverter.convertMoveFrom(move);
		
		logger.info("player's " + this.playerId + "  current position: " + this.currPos.getX() + " " + this.currPos.getY());
	
		Coordinates target = fullMap.getTargetCoordinatesFromMove(this.currPos, move);
		if (this.currentNoOfStepsToTake == 0) {
			
			this.currentDirection = newMove;
			this.currentNoOfStepsToTake = fullMap.getPathWeight(this.currPos, target);
			
		} else if (newMove != this.currentDirection) {
			
			this.currentDirection = newMove;
			this.currentNoOfStepsToTake = fullMap.getPathWeight(this.currPos, target);
		}
		
		this.currentNoOfStepsToTake -= 1;
		
		if (this.currentNoOfStepsToTake == 0) {
			this.currPos = target;
		}
		
	}
	
	/*
	 *  If player landed on its own treasure field
	 *  it picks it up and the treasure disappears
	 */
	public void updateTreasureStatus(GameData game) {
		Coordinates myTreasurePos = this.getTreasurePos();
		if (this.currPos.equals(myTreasurePos)) {
			this.setHasCollectedTreasure(true);
			this.setShowTreasure(false);
		} 	 
	}
	
	/*
	 *  If player landed on enemy Fort position
	 *  If it has already picked the treasure it wins
	 */
	public void updateEnemyFortStatus(GameData game) {

		Player enemy = game.getTheOtherPlayer(this.getPlayerId());
		Coordinates enemyFortPos = enemy.getFortPos();
		
		if (this.currPos.equals(enemyFortPos)) {
			this.setShowEnemyFort(true);
		}
		
		if (this.currPos.equals(enemyFortPos) && this.isHasCollectedTreasure()) {
			game.setWinner(enemy.getPlayerId());
		}
		
	}
	
	/*
	 *  Update player's ability to see treasure and forts
	 */
	public void updateMountainViewStatus(GameData game, FullMapHandler fullMap) {
		
		Coordinates myTreasurePos = this.getTreasurePos();
		Player enemy = game.getTheOtherPlayer(this.getPlayerId());
		Coordinates enemyFortPos = enemy.getFortPos();
		
		if (fullMap.getFields().get(this.currPos).getFieldType() == MapFieldType.MOUNTAIN) {
			
			if (fullMap.checkFortsAroundMountain(enemyFortPos, this.currPos)) {
				this.setShowEnemyFort(true);
			}
			
			if (this.isHasCollectedTreasure() == false) {
				if (fullMap.checkTreasuresAroundMountain(myTreasurePos, this.currPos)) {
					//logger.warn("I, " + this.playerId + " am on a mountain and I am here " + this.currPos.getX() + " " + this.currPos.getY());
					//logger.warn("and I have discovered my treasure here: " + myTreasurePos.getX() + " " + myTreasurePos.getY());
					this.setShowTreasure(true);	
				}
			}

		}
	
	}

}
