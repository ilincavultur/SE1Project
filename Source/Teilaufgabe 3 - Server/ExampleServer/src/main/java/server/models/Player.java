package server.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

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
	
	/*
	 * 	Get number of moves to enter/escape the field
	 */
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
		Coordinates toReturn = new Coordinates();
		Map<Coordinates, MapNode> fields = game.getFullMap().getFields();
		
		if (move.getMove() == EMove.Up) {
			Coordinates direction = pos.getUpNeighbour(fields);
			return direction;
		}
		
		if (move.getMove() == EMove.Down) {
			Coordinates direction = pos.getDownNeighbour(fields);
			return direction;
		}
		
		if (move.getMove() == EMove.Left) {
			Coordinates direction = pos.getLeftNeighbour(fields);
			return direction;
		}
		
		if (move.getMove() == EMove.Right) {
			Coordinates direction = pos.getRightNeighbour(fields);
			return direction;
		}
		
		return toReturn;
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
		Coordinates myTreasurePos = this.getTreasurePos();
		if (this.currPos.equals(myTreasurePos)) {
			this.setHasCollectedTreasure(true);
			this.setShowTreasure(false);
		} 	 
	}
	
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
	 * if a neighbour has wrong coordinates (lower than 0), it is simply not added to the map
	 * 	if all neighnours are wrong, the returned Map is empty
	 */
	public Map<String, Coordinates> getFieldsAroundMountain(Coordinates mountainPos, Map<Coordinates, MapNode> fields) {
		
		Map<String, Coordinates> toReturn = new HashMap<String, Coordinates>();
		
		Coordinates up = mountainPos.getUpNeighbour(fields);
		if (up.isCoordinateValid()) {
			if (fields.get(up).getFieldType() != MapFieldType.WATER) {
				toReturn.put("up", mountainPos.getUpNeighbour(fields));	
			}
		}
		
		Coordinates nw = mountainPos.getNorthWestNeighbour(fields);
		if (nw.isCoordinateValid()) {
			if (fields.get(nw).getFieldType() != MapFieldType.WATER) {
				toReturn.put("nw", mountainPos.getNorthWestNeighbour(fields));	
			}
		}
		
		Coordinates down = mountainPos.getDownNeighbour(fields);
		if (down.isCoordinateValid()) {
			if (fields.get(down).getFieldType() != MapFieldType.WATER) {
				toReturn.put("down", mountainPos.getDownNeighbour(fields));	
			}
		}
		
		Coordinates ne = mountainPos.getNorthEastNeighbour(fields);
		if (ne.isCoordinateValid()) {
			if (fields.get(ne).getFieldType() != MapFieldType.WATER) {
				toReturn.put("ne", mountainPos.getNorthEastNeighbour(fields));	
			}
		}
		
		Coordinates left = mountainPos.getLeftNeighbour(fields);
		if (left.isCoordinateValid()) {
			if (fields.get(left).getFieldType() != MapFieldType.WATER) {
				toReturn.put("left", mountainPos.getLeftNeighbour(fields));		
			}
		}
		
		Coordinates se = mountainPos.getSouthEastNeighbour(fields);
		if (se.isCoordinateValid()) {
			if (fields.get(se).getFieldType() != MapFieldType.WATER) {
				toReturn.put("se", mountainPos.getSouthEastNeighbour(fields));	
			}
		}
	
		Coordinates right = mountainPos.getRightNeighbour(fields);
		if (right.isCoordinateValid()) {
			if (fields.get(right).getFieldType() != MapFieldType.WATER) {
				toReturn.put("right", mountainPos.getRightNeighbour(fields));	
			}
		}
		
		Coordinates sw = mountainPos.getSouthWestNeighbour(fields);
		if (sw.isCoordinateValid()) {
			if (fields.get(sw).getFieldType() != MapFieldType.WATER) {
				toReturn.put("sw", mountainPos.getSouthWestNeighbour(fields));	
			}
		}
		
		return toReturn;	
	}
	
	/*
	 *  Check if treasure is seen from current mountain position
	 */
	public boolean checkTreasuresAroundMountain(Coordinates myTreasurePos, Coordinates mountainPos, Map<Coordinates, MapNode> fields) {
		
		Map<String, Coordinates> fieldsAround = getFieldsAroundMountain(mountainPos, fields);
		
		if (fieldsAround.containsValue(myTreasurePos)) {
			return true;
		}

		return false;
		
	}
	
	/*
	 *  Check if fort is seen from current mountain position
	 */
	public boolean checkFortsAroundMountain(Coordinates enemyFortPos, Coordinates mountainPos, Map<Coordinates, MapNode> fields) {
		
		Map<String, Coordinates> fieldsAround = getFieldsAroundMountain(mountainPos, fields);
		
		if (fieldsAround.containsValue(enemyFortPos)) {
			return true;
		}

		return false;
	}
	
	/*
	 *  Update player's ability to see treasure and forts
	 */
	public void updateMountainViewStatus(GameData game) {
		
		Coordinates myTreasurePos = this.getTreasurePos();
		Player enemy = game.getTheOtherPlayer(this.getPlayerId());
		Coordinates enemyFortPos = enemy.getFortPos();
		
		if (game.getFullMap().getFields().get(this.currPos).getFieldType() == MapFieldType.MOUNTAIN) {
			
			if (checkFortsAroundMountain(enemyFortPos, this.currPos, game.getFullMap().getFields())) {
				this.setShowEnemyFort(true);
			}
			
			if (this.isHasCollectedTreasure() == false) {
				if (checkTreasuresAroundMountain(myTreasurePos, this.currPos, game.getFullMap().getFields())) {
					this.setShowTreasure(true);	
				}
			}

		}
	
	}

}
