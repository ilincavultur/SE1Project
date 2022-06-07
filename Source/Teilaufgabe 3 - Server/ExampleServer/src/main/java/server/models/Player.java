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
import server.enums.TreasureState;
import server.network.NetworkConverter;

public class Player {

	String playerId;
	InternalHalfMap halfMap;
	Coordinates currPos;
	boolean hasCollectedTreasure;
	PlayerRegistration playerReg;
	private boolean showEnemyFort;
	private boolean showTreasure;
	private MoveCommand currentDirection;
	private int currentNoOfStepsToTake;
	
	
	private static final Logger logger = LoggerFactory.getLogger(Player.class);
	
	
	public Player() {
		super();
		Coordinates currPos = new Coordinates(0,0);
		this.currPos = currPos;
		this.showEnemyFort = false;
		this.currentNoOfStepsToTake = 0;
		this.hasCollectedTreasure = false;
		this.showTreasure = false;
		
		
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

	
	public boolean isShowTreasure() {
		return showTreasure;
	}
	public void setShowTreasure(boolean showTreasure) {
		this.showTreasure = showTreasure;
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
		
		// direction
		MoveCommand newMove = networkConverter.convertMoveFrom(move);
		logger.info("newMove " + move.getMove().toString());
		Coordinates target = getTargetCoordinatesFromMove(game, this.currPos, move);
		if (this.currentNoOfStepsToTake == 0) {
			this.currentDirection = newMove;
			this.currentNoOfStepsToTake = getPathWeight(this, game, this.currPos, target);
		} else if (newMove != this.currentDirection) {
			
			this.currentDirection = newMove;
			this.currentNoOfStepsToTake = getPathWeight(this, game, this.currPos, target);
		}
		
		this.currentNoOfStepsToTake -= 1;
		
		if (this.currentNoOfStepsToTake == 0) {
			this.currPos = target;
		}
		
	}
	
	public void updateTreasureStatus(GameData game) {
		if (game.getFullMap().getFields().get(this.currPos).getTreasureState() == TreasureState.MYTREASURE) {
			this.setHasCollectedTreasure(true);
			this.setShowTreasure(false);
		}
	}

}
