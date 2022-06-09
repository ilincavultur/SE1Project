package server.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.LoggerFactory;

import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromServer.FullMapNode;
import ch.qos.logback.classic.Logger;
import server.enums.MapFieldType;
import server.enums.TreasureState;
import server.network.NetworkConverter;

public class InternalFullMap {
	
	private Map<Coordinates, MapNode> fields = new HashMap<Coordinates, MapNode>();
	private int xSize;
	private int ySize;
	private String firstMap;
	
	private static final Logger logger = (Logger) LoggerFactory.getLogger(InternalFullMap.class);

	public InternalFullMap() {
		super();
		setupFullMap();
	}

	public Map<Coordinates, MapNode> getFields() {
		return fields;
	}

	public void setFields(Map<Coordinates, MapNode> fields) {
		this.fields = fields;
	}

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
	
	// choose which half Map is the first one
	public String pickFirstHalf() {
		
		Random randomNo = new Random();
		int no = randomNo.nextInt(2);
		if (no == 0) {
			return "first";
		}
		return "second";
		
	}

	public void setupFullMap() {
		
		pickDimensions();
		
		this.firstMap = pickFirstHalf();
	
	}
	
	public void assembleFullMap(GameData game, List<Player> players, InternalHalfMap halfMap1, InternalHalfMap halfMap2) {
		
		if (this.firstMap.equals("first")) {
			
			fields.putAll(halfMap1.getFields());
		
			players.get(0).setFortPos(halfMap1.getFortPos());
			players.get(0).setTreasurePos(halfMap1.getTreasurePos());
		
			transformCoordinates(players.get(1), halfMap2);
			
			if (game.isChanged() == false) {
				game.setChanged(true);
				
				players.get(0).setCurrPos(halfMap1.getFortPos());
				players.get(1).setCurrPos(halfMap2.getFortPos());
			}
			
		} else {
			
			fields.putAll(halfMap2.getFields());
	
			players.get(1).setFortPos(halfMap2.getFortPos());
			players.get(1).setTreasurePos(halfMap2.getTreasurePos());
			
			transformCoordinates(players.get(0), halfMap1);

			if (game.isChanged() == false) {
				game.setChanged(true);
				
				players.get(0).setCurrPos(halfMap1.getFortPos());
				players.get(1).setCurrPos(halfMap2.getFortPos());
			} 
	
		}
		
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


	public void transformCoordinates(Player player, InternalHalfMap halfMap) {
		
		// square
		if (this.xSize == 8) {
		// y + 4
			 
			Coordinates oldFortPos = halfMap.getFortPos();
			Coordinates oldTreasurePos = halfMap.getTreasurePos();
			
			for( Map.Entry<Coordinates, MapNode> mapEntry : halfMap.getFields().entrySet() ) {
				
				Coordinates newPos = new Coordinates(mapEntry.getKey().getX(), mapEntry.getKey().getY() + 4);
				
				if (mapEntry.getKey().equals(oldTreasurePos)) {
					player.setTreasurePos(newPos);
				}
				
				mapEntry.getValue().setPosition(newPos);
				
				if (mapEntry.getKey().equals(oldFortPos)) {
					
					halfMap.setFortPos(newPos);
					
					player.setFortPos(newPos);
				}
					
				fields.put(newPos, mapEntry.getValue());
			}
		} else if (this.xSize == 16) {
		// x + 8
		
			Coordinates oldFortPos = halfMap.getFortPos();
			Coordinates oldTreasurePos = halfMap.getTreasurePos();
			
			for( Map.Entry<Coordinates, MapNode> mapEntry : halfMap.getFields().entrySet() ) {
				
				Coordinates newPos = new Coordinates(mapEntry.getKey().getX() + 8, mapEntry.getKey().getY());
				
				if (mapEntry.getKey().equals(oldTreasurePos)) {
					player.setTreasurePos(newPos);
				}
				
				mapEntry.getValue().setPosition(newPos);
				
				if (mapEntry.getKey().equals(oldFortPos)) {
					
					halfMap.setFortPos(newPos);
					
					player.setFortPos(newPos);
					
				}
					
				fields.put(newPos, mapEntry.getValue());
			}
		}
	}
	
}
