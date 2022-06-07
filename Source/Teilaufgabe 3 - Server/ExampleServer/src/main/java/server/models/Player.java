package server.models;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.MessagesFromClient.EMove;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import server.controllers.GameStateController;
import server.enums.MapFieldType;
import server.enums.MoveCommand;
import server.network.NetworkConverter;

public class Player {

	String playerId;
	InternalHalfMap halfMap;
	Coordinates currPos;
	boolean hasCollectedTreasure;
	PlayerRegistration playerReg;
	private boolean showEnemyFort;
	private MoveCommand currentDirection;
	private int currentNoOfStepsToTake;
	private Coordinates currentTarget;
	private boolean firstMoveEver;
	private static final Logger logger = LoggerFactory.getLogger(Player.class);
	private int stepsTaken;
	
	public Player() {
		super();
		Coordinates currPos = new Coordinates(0,0);
		this.currPos = currPos;
		this.showEnemyFort = false;
		this.currentNoOfStepsToTake = 0;
		this.currentTarget = new Coordinates();
		this.firstMoveEver = true;
		this.stepsTaken = 0;
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
	public boolean isShowEnemyFort() {
		return showEnemyFort;
	}
	public void setShowEnemyFort(boolean showEnemyFort) {
		this.showEnemyFort = showEnemyFort;
	}
	public boolean isPlayersHalfMapPresent() {
		return this.getHalfMap() != null;
	}
	public MoveCommand getCurrentDirection() {
		return currentDirection;
	}

	public void setCurrentDirection(MoveCommand currentDirection) {
		this.currentDirection = currentDirection;
	}
	public int getCurrentNoOfStepsToTake() {
		return currentNoOfStepsToTake;
	}
	public void setCurrentNoOfStepsToTake(int currentNoOfStepsToTake) {
		this.currentNoOfStepsToTake = currentNoOfStepsToTake;
	}
	public Coordinates getCurrentTarget() {
		return currentTarget;
	}
	public void setCurrentTarget(Coordinates currentTarget) {
		this.currentTarget = currentTarget;
	}
	public boolean isFirstMoveEver() {
		return firstMoveEver;
	}
	public void setFirstMoveEver(boolean firstMoveEver) {
		this.firstMoveEver = firstMoveEver;
	}
	
	public int getStepsTaken() {
		return stepsTaken;
	}
	public void setStepsTaken(int stepsTaken) {
		this.stepsTaken = stepsTaken;
	}
	private int getMoves(MapNode field) {
		if (field.getFieldType() == MapFieldType.MOUNTAIN) {
			return 2;
		} 
		return 1;
	}
	
	
	private int getPathWeight(Player player, GameData game, Coordinates currentField, Coordinates nextField) {

		int firstFieldMoves = this.getMoves(game.getFullMap().getFields().get(currentField)); 
		int secondFieldMoves = this.getMoves(game.getFullMap().getFields().get(nextField)); 
 		
		return firstFieldMoves + secondFieldMoves;
		
	}
	
	private Coordinates getTargetCoordinatesFromMove(GameData game, Coordinates pos, PlayerMove move) {
		Coordinates toRet = new Coordinates();
		Map<Coordinates, MapNode> fields = game.getFullMap().getFields();
		
		if (move.getMove() == EMove.Up) {
			Coordinates dir = pos.getUpNeighbour(fields);
			return dir;
		}
		
		if (move.getMove() == EMove.Down) {
			Coordinates dir = pos.getDownNeighbour(fields);
			return dir;
		}
		
		if (move.getMove() == EMove.Left) {
			Coordinates dir = pos.getLeftNeighbour(fields);
			return dir;
		}
		
		if (move.getMove() == EMove.Right) {
			Coordinates dir = pos.getRightNeighbour(fields);
			return dir;
		}
		
		return toRet;
	}
	
	public void processMove(GameData game, UniqueGameIdentifier gameID, PlayerMove move, NetworkConverter networkConverter) {
		

		MoveCommand newMove = networkConverter.convertMoveFrom(move);
		
		logger.info("newMove " + move.getMove().toString());
		
		Coordinates target = getTargetCoordinatesFromMove(game, this.currPos, move);
		logger.info("target pos " + target.getX() + " " + target.getY());
		logger.info("target pos field type " + game.getFullMap().getFields().get(target).getFieldType().toString() );
		
		Coordinates currentPlayersTarget = this.currentTarget;
		logger.info("player's target pos " + currentPlayersTarget.getX() + " " + currentPlayersTarget.getY());
		logger.info("player's target pos field type " + game.getFullMap().getFields().get(currentPlayersTarget).getFieldType().toString() );
		
		// if we have reached the target => we need to update the current position of the player
		
		// if it s just the first time we receive the move, not
		logger.info("current pos " + this.getCurrPos().getX() + " " + this.currPos.getY());
		logger.info("current pos field type " + game.getFullMap().getFields().get(this.currPos).getFieldType().toString() );
		// first move to new direction or we have reached the target
		if (this.currentNoOfStepsToTake == 0) {
			
			if (this.isFirstMoveEver()) { // first move ever
				logger.info("first move ever for player: " + this.getPlayerId());
				int noOfStepsNeeded = getPathWeight(this, game, this.currPos, target);
				this.currentDirection = networkConverter.convertMoveFrom(move);
				this.currentNoOfStepsToTake = noOfStepsNeeded-1;
				this.currentTarget = target;
				
				this.firstMoveEver = false;
				
			} else { // reached target
				logger.info("REACHED TARGET");
				this.currPos.setX(this.currentTarget.getX());
				this.currPos.setY(this.currentTarget.getY());
				//this.setCurrPos(this.getCurrentTarget());
				
				logger.info("new curr pos for player: " + this.getPlayerId() + " " + this.currPos.getX() + " "  + this.currPos.getY());
				logger.info("new curr pos for player: " + this.getPlayerId());
				
				int noOfStepsNeeded = getPathWeight(this, game, this.currPos, target);
				this.currentDirection = networkConverter.convertMoveFrom(move);
				this.currentNoOfStepsToTake = noOfStepsNeeded-1;
				
				this.currentTarget = target;
			}	
			
		} else {
			if (newMove == this.currentDirection) {
				logger.info("SAME DIRECTION");
				int steps = this.currentNoOfStepsToTake;
				steps -= 1;
				this.currentNoOfStepsToTake = steps;
				if (this.currentNoOfStepsToTake == 0) {
					this.currPos.setX(this.currentTarget.getX());
					this.currPos.setY(this.currentTarget.getY());
				}
			
			} else {
				logger.info("NEW DIRECTION");
				int noOfStepsNeeded = getPathWeight(this, game, this.currPos, target);
				this.currentDirection = networkConverter.convertMoveFrom(move);
				this.currentNoOfStepsToTake = noOfStepsNeeded-1;
				
				this.currentTarget = target;
				
				
			}
			
		}
		
		if (this.currentNoOfStepsToTake == 0) {
			this.currPos.setX(this.currentTarget.getX());
			this.currPos.setY(this.currentTarget.getY());
		}
		
		
	}

}
