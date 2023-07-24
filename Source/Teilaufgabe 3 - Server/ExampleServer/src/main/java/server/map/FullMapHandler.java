package server.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.slf4j.LoggerFactory;

import MessagesBase.MessagesFromClient.EMove;
import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromServer.FullMapNode;
import ch.qos.logback.classic.Logger;
import server.enums.MapFieldType;
import server.enums.TreasureState;
import server.game.GameData;
import server.network.NetworkConverter;
import server.player.Player;

public class FullMapHandler {
	
	private Map<Coordinates, MapNode> fields = new HashMap<Coordinates, MapNode>();
	private int xSize;
	private int ySize;
	private String firstMap;
	
	private static final Logger logger = (Logger) LoggerFactory.getLogger(FullMapHandler.class);

	public FullMapHandler() {
		super();
		setupFullMap();
	}

	public Map<Coordinates, MapNode> getFields() {
		return fields;
	}

	public void setFields(Map<Coordinates, MapNode> fields) {
		this.fields = fields;
	}

	/*
	 *  Choose random dimension of the map
	 */
	public void pickDimensions() {
		
		Random randomNo = new Random();
		int no = randomNo.nextInt(2);
		if (no == 0) {
			this.xSize = 8;
			this.ySize = 8;
		} else {
			this.xSize = 16;
			this.ySize = 4;
		}
		
	}
	
	/*
	 *  Choose which will be the first half (from left to right / from up to down)
	 */
	public String pickFirstHalf() {
		
		Random randomNo = new Random();
		int no = randomNo.nextInt(2);
		if (no == 0) {
			return "first";
		}
		return "second";
		
	}

	/*
	 *  Choose random dimension of the map
	 *  Choose which will be the first half (from left to right / from up to down)
	 */
	public void setupFullMap() {
		pickDimensions();
		this.firstMap = pickFirstHalf();
	}
	
	private void setFields (GameData game, Player player1, Player player2, Optional<InternalHalfMap> halfMap1, Optional<InternalHalfMap> halfMap2) {
		fields.putAll(halfMap1.get().getFields());
		
		player1.setFortPos(halfMap1.get().getFortPosition());
		player1.setTreasurePosition(halfMap1.get().getTreasurePosition());
	
		transformCoordinates(player2, halfMap2.get());
		
		if (game.isChanged() == false) {
			game.setChanged(true);
			
			player1.setCurrentPosition(halfMap1.get().getFortPosition());
			player2.setCurrentPosition(halfMap2.get().getFortPosition());
		}
	}
	
	public void assembleFullMap(GameData game, List<Player> players, Optional<InternalHalfMap> halfMap1, Optional<InternalHalfMap> halfMap2) {
		
		if (this.firstMap.equals("first")) {
			setFields(game, players.get(0), players.get(1), halfMap1, halfMap2);
		} else {
			setFields(game, players.get(1), players.get(0), halfMap2, halfMap1);
		}
		logger.info("player's " + players.get(0).getPlayerId() + "  treasure position: " + players.get(0).getTreasurePosition().getX() + " " + players.get(0).getTreasurePosition().getY());
		logger.info("player's " + players.get(1).getPlayerId() + "  treasure position: " + players.get(1).getTreasurePosition().getX() + " " + players.get(1).getTreasurePosition().getY());
	}
	
	public int getxSize() {
		return xSize;
	}


	public void setxSize(int xSize) {
		this.xSize = xSize;
	}


	public int getySize() {
		return ySize;
	}


	public void setySize(int ySize) {
		this.ySize = ySize;
	}

	/*
	 *  Transform coordinates of the "second to come" map
	 */
	public void transformCoordinates(Player player, InternalHalfMap halfMap) {
		
		Coordinates oldFortPosition = halfMap.getFortPosition();
		Coordinates oldTreasurePosition = halfMap.getTreasurePosition();
		
		for( var eachNode : halfMap.getFields().entrySet() ) {
			
			Coordinates newPosition = eachNode.getKey();
			
			// square map
			if (this.xSize == 8) {
				newPosition = new Coordinates(eachNode.getKey().getX(), eachNode.getKey().getY() + 4);
			} else if (this.xSize == 16) {
				newPosition = new Coordinates(eachNode.getKey().getX() + 8, eachNode.getKey().getY());
			}
			
			if (eachNode.getKey().equals(oldTreasurePosition)) {
				player.setTreasurePosition(newPosition);
			}
			
			eachNode.getValue().setPosition(newPosition);
			
			if (eachNode.getKey().equals(oldFortPosition)) {
				halfMap.setFortPos(newPosition);
				player.setFortPos(newPosition);
			}
				
			fields.put(newPosition, eachNode.getValue());
		}
		
	}
	
	/*	
	 * if a neighbour has wrong coordinates (lower than 0), it is simply not added to the map
	 * 	if all neighnours are wrong, the returned Map is empty
	 */
	public Map<String, Coordinates> getFieldsAroundMountain(Coordinates mountainPosition) {
		
		Map<String, Coordinates> toReturn = new HashMap<String, Coordinates>();
		
		Coordinates up = mountainPosition.getUpNeighbour(fields);
		if (up.isCoordinateValid()) {
			if (fields.get(up).getFieldType() != MapFieldType.WATER) {
				toReturn.put("up", mountainPosition.getUpNeighbour(fields));	
			}
		}
		
		Coordinates nw = mountainPosition.getNorthWestNeighbour(fields);
		if (nw.isCoordinateValid()) {
			if (fields.get(nw).getFieldType() != MapFieldType.WATER) {
				toReturn.put("nw", mountainPosition.getNorthWestNeighbour(fields));	
			}
		}
		
		Coordinates down = mountainPosition.getDownNeighbour(fields);
		if (down.isCoordinateValid()) {
			if (fields.get(down).getFieldType() != MapFieldType.WATER) {
				toReturn.put("down", mountainPosition.getDownNeighbour(fields));	
			}
		}
		
		Coordinates ne = mountainPosition.getNorthEastNeighbour(fields);
		if (ne.isCoordinateValid()) {
			if (fields.get(ne).getFieldType() != MapFieldType.WATER) {
				toReturn.put("ne", mountainPosition.getNorthEastNeighbour(fields));	
			}
		}
		
		Coordinates left = mountainPosition.getLeftNeighbour(fields);
		if (left.isCoordinateValid()) {
			if (fields.get(left).getFieldType() != MapFieldType.WATER) {
				toReturn.put("left", mountainPosition.getLeftNeighbour(fields));		
			}
		}
		
		Coordinates se = mountainPosition.getSouthEastNeighbour(fields);
		if (se.isCoordinateValid()) {
			if (fields.get(se).getFieldType() != MapFieldType.WATER) {
				toReturn.put("se", mountainPosition.getSouthEastNeighbour(fields));	
			}
		}
	
		Coordinates right = mountainPosition.getRightNeighbour(fields);
		if (right.isCoordinateValid()) {
			if (fields.get(right).getFieldType() != MapFieldType.WATER) {
				toReturn.put("right", mountainPosition.getRightNeighbour(fields));	
			}
		}
		
		Coordinates sw = mountainPosition.getSouthWestNeighbour(fields);
		if (sw.isCoordinateValid()) {
			if (fields.get(sw).getFieldType() != MapFieldType.WATER) {
				toReturn.put("sw", mountainPosition.getSouthWestNeighbour(fields));	
			}
		}
		
		return toReturn;	
	}
	
	/*
	 *  Check if treasure is seen from current mountain position
	 */
	public boolean checkTreasuresAroundMountain(Coordinates myTreasurePos, Coordinates mountainPosition) {
		
		Map<String, Coordinates> fieldsAround = getFieldsAroundMountain(mountainPosition);
		
		if (fieldsAround.containsValue(myTreasurePos)) {
			return true;
		}

		return false;
		
	}
	
	/*
	 *  Check if fort is seen from current mountain position
	 */
	public boolean checkFortsAroundMountain(Coordinates enemyFortPos, Coordinates mountainPosition) {
		
		Map<String, Coordinates> fieldsAround = getFieldsAroundMountain(mountainPosition);
		
		if (fieldsAround.containsValue(enemyFortPos)) {
			return true;
		}

		return false;
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

	public int getPathWeight(Coordinates currentField, Coordinates nextField) {

		int firstFieldMoves = getMoves(fields.get(currentField)); 
		int secondFieldMoves = getMoves(fields.get(nextField)); 
 		
		return firstFieldMoves + secondFieldMoves;
		
	}
	
	public Coordinates getTargetCoordinatesFromMove(Coordinates currentPosition, PlayerMove move) {
		Coordinates toReturn = new Coordinates();

		if (move.getMove() == EMove.Up) {
			Coordinates direction = currentPosition.getUpNeighbour(fields);
			return direction;
		}
		
		if (move.getMove() == EMove.Down) {
			Coordinates direction = currentPosition.getDownNeighbour(fields);
			return direction;
		}
		
		if (move.getMove() == EMove.Left) {
			Coordinates direction = currentPosition.getLeftNeighbour(fields);
			return direction;
		}
		
		if (move.getMove() == EMove.Right) {
			Coordinates direction = currentPosition.getRightNeighbour(fields);
			return direction;
		}
		
		return toReturn;
	}
	
	/*
	 * 	Gets a random enemy position for the first 10 rounds
	 */
	public Coordinates getRandomEnemyPos() {

		Random randomNo = new Random();
		
		if (this.getxSize() == 8) {
			int randomFortX = randomNo.nextInt(8);
			int randomFortY = randomNo.nextInt(8);

			return new Coordinates(randomFortX, randomFortY);
		}
		
		int randomFortX = randomNo.nextInt(16);
		int randomFortY = randomNo.nextInt(4);
		
		return new Coordinates(randomFortX, randomFortY);
	}
	
}
