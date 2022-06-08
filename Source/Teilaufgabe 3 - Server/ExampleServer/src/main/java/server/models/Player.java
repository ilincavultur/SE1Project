package server.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.MessagesFromClient.EMove;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import server.controllers.GameStateController;
import server.enums.FortState;
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
	private Coordinates treasurePos;
	private Coordinates fortPos;
	
	private static final Logger logger = LoggerFactory.getLogger(Player.class);
	
	
	public Player() {
		super();
		Coordinates currPos = new Coordinates(0,0);
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
		if (game.getFullMap().getFields().get(this.currPos).getFieldType() != MapFieldType.MOUNTAIN) {
			this.setShowTreasure(false);
		}
		
		Coordinates myTreasurePos = this.getTreasurePos();
		if (this.currPos.equals(myTreasurePos)) {
		//if (game.getFullMap().getFields().get(this.currPos).getTreasureState() == TreasureState.MYTREASURE) {
			this.setHasCollectedTreasure(true);
			this.setShowTreasure(false);
		} 
		 
		 
	}
	
	public void updateEnemyFortStatus(GameData game) {
		
		if (game.getFullMap().getFields().get(this.currPos).getFieldType() != MapFieldType.MOUNTAIN) {
			this.setShowEnemyFort(false);
		}
		
		Player enemy = game.getTheOtherPlayer(this.getPlayerId());
		Coordinates enemyFortPos = enemy.getFortPos();
		
		if (this.currPos.equals(enemyFortPos)) {
			this.setShowEnemyFort(true);
		}
		
		if (this.currPos.equals(enemyFortPos) && this.isHasCollectedTreasure()) {
			game.setWinner(enemy.getPlayerId());
		}
		
	}
	
	public Map<String, Coordinates> getFieldsAroundMountain(Coordinates mountainPos, Map<Coordinates, MapNode> fields) {
		
		Map<String, Coordinates> toReturn = new HashMap<String, Coordinates>();
		
		if (fields.get(mountainPos.getUpNeighbour(fields)) != null) {
			if (fields.get(mountainPos.getUpNeighbour(fields)).getFieldType() != MapFieldType.WATER) {
				toReturn.put("up", mountainPos.getUpNeighbour(fields));	
			}
		}
		
		if (fields.get(mountainPos.getNorthWestNeighbour(fields)) != null) {
			if (fields.get(mountainPos.getNorthWestNeighbour(fields)).getFieldType() != MapFieldType.WATER) {
				toReturn.put("nw", mountainPos.getNorthWestNeighbour(fields));	
			}
		}
		
		if (fields.get(mountainPos.getDownNeighbour(fields)) != null) {
			if (fields.get(mountainPos.getDownNeighbour(fields)).getFieldType() != MapFieldType.WATER) {
				toReturn.put("down", mountainPos.getDownNeighbour(fields));	
			}
		}
		
		if (fields.get(mountainPos.getNorthEastNeighbour(fields)) != null) {
			if (fields.get(mountainPos.getNorthEastNeighbour(fields)).getFieldType() != MapFieldType.WATER) {
				toReturn.put("ne", mountainPos.getNorthEastNeighbour(fields));	
			}
		}
		
		if (fields.get(mountainPos.getLeftNeighbour(fields)) != null) {
			if (fields.get(mountainPos.getLeftNeighbour(fields)).getFieldType() != MapFieldType.WATER) {
				toReturn.put("left", mountainPos.getLeftNeighbour(fields));		
			}
		}
		
		if (fields.get(mountainPos.getSouthEastNeighbour(fields)) != null) {
			if (fields.get(mountainPos.getSouthEastNeighbour(fields)).getFieldType() != MapFieldType.WATER) {
				toReturn.put("se", mountainPos.getSouthEastNeighbour(fields));	
			}
		}
	
		if (fields.get(mountainPos.getRightNeighbour(fields)) != null) {
			if (fields.get(mountainPos.getRightNeighbour(fields)).getFieldType() != MapFieldType.WATER) {
				toReturn.put("right", mountainPos.getRightNeighbour(fields));	
			}
		}
		
		if (fields.get(mountainPos.getSouthWestNeighbour(fields)) != null) {
			if (fields.get(mountainPos.getSouthWestNeighbour(fields)).getFieldType() != MapFieldType.WATER) {
				toReturn.put("sw", mountainPos.getSouthWestNeighbour(fields));	
			}
		}
		
		
		return toReturn;	
	}
	
	public boolean checkTreasuresAroundMountain(Coordinates myTreasurePos, Coordinates mountainPos, Map<Coordinates, MapNode> fields) {
	
		logger.info("current mountainPos " + mountainPos.getX() + " " + mountainPos.getY());
		
		Map<String, Coordinates> fieldsAround = getFieldsAroundMountain(mountainPos, fields);
		
		for( Map.Entry<String, Coordinates> mapEntry : fieldsAround.entrySet() ) {
			if (mapEntry.getValue().equals(myTreasurePos)) {
			//if (fields.get(mapEntry.getValue()).getTreasureState() == TreasureState.MYTREASURE) {
				//logger.info("treasure field " + mapEntry.getValue().getX() + " " + mapEntry.getValue().getY());
				return true;
			} else {
				//logger.info("field around " + mapEntry.getValue().getX() + " " + mapEntry.getValue().getY());
			}
		}
		
		return false;
		
	}
	
	public boolean checkFortsAroundMountain(Coordinates enemyFortPos, Coordinates mountainPos, Map<Coordinates, MapNode> fields) {
		logger.info("current mountainPos " + mountainPos.getX() + " " + mountainPos.getY());
		
		
		Map<String, Coordinates> fieldsAround = getFieldsAroundMountain(mountainPos, fields);
		
		for( Map.Entry<String, Coordinates> mapEntry : fieldsAround.entrySet() ) {
			//if (fields.get(mapEntry.getValue()).getFortState() == FortState.ENEMYFORT) {
			if (mapEntry.getValue().equals(enemyFortPos)) {
				logger.info("fort field " + mapEntry.getValue().getX() + " " + mapEntry.getValue().getY());
				return true;
			} else {
				logger.info("field around " + mapEntry.getValue().getX() + " " + mapEntry.getValue().getY());
			}
		
		}
		return false;
	}
	
	public void updateMountainViewStatus(GameData game) {
		Coordinates myTreasurePos = this.getTreasurePos();
		Player enemy = game.getTheOtherPlayer(this.getPlayerId());
		Coordinates enemyFortPos = enemy.getFortPos();
		logger.info("enemy fort pos should be:  " + enemyFortPos.getX() + "  " + enemyFortPos.getY());
		
		if (game.getFullMap().getFields().get(this.currPos).getFieldType() == MapFieldType.MOUNTAIN) {
			if (this.isHasCollectedTreasure() == false) {
				if (checkTreasuresAroundMountain(myTreasurePos, this.currPos, game.getFullMap().getFields())) {
					logger.info("found a treasure around mountain: " + this.currPos.getX() + "  " + this.currPos.getY());
				
					this.setShowTreasure(true);	
					
				}
			}
			//if (this.isShowEnemyFort() == false) {
				if (checkFortsAroundMountain(enemyFortPos, this.currPos, game.getFullMap().getFields())) {
					logger.info("found a fort around mountain!" + this.currPos.getX() + "  " + this.currPos.getY());
					this.setShowEnemyFort(true);	
					
				} else {
					this.setShowEnemyFort(false);
				}
			//}
			
			
		}
	
	}
	
	

}
