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
			//logger.info("first");
			players.get(0).setFortPos(halfMap1.getFortPos());
			players.get(0).setTreasurePos(halfMap1.getTreasurePos());
			
			//logger.info("first players pos : " + players.get(0).getTreasurePos().getX() + " " + players.get(0).getTreasurePos().getY());
			
			//logger.info("second players pos before : " + players.get(1).getHalfMap().getTreasurePos().getX() + " " + players.get(1).getHalfMap().getTreasurePos().getY());
			/*logger.info("first players fort pos : " + players.get(0).getFortPos().getX() + " " + players.get(0).getFortPos().getY());
			
			logger.info("second players fort pos before : " + players.get(1).getHalfMap().getFortPos().getX() + " " + players.get(1).getHalfMap().getFortPos().getY());
			*/
			transformCoordinates(players.get(1), halfMap2);
			
			//logger.info("second players pos AFTER: " + players.get(1).getTreasurePos().getX() + " " + players.get(1).getTreasurePos().getY());
			//logger.info("second players fort pos AFTER: " + players.get(1).getFortPos().getX() + " " + players.get(1).getFortPos().getY());
			
			
			if (game.isChanged() == false) {
				game.setChanged(true);
				
				players.get(0).setCurrPos(halfMap1.getFortPos());
				players.get(1).setCurrPos(halfMap2.getFortPos());
			}
			
		} else {
			fields.putAll(halfMap2.getFields());
			//logger.info("second");
			
			players.get(1).setFortPos(halfMap2.getFortPos());
			players.get(1).setTreasurePos(halfMap2.getTreasurePos());
			
			//logger.info("first players pos : " + players.get(1).getTreasurePos().getX() + " " + players.get(1).getTreasurePos().getY());
			
			//logger.info("second players pos before : " + players.get(0).getHalfMap().getTreasurePos().getX() + " " + players.get(0).getHalfMap().getTreasurePos().getY());
			/*logger.info("first players fort pos : " + players.get(1).getFortPos().getX() + " " + players.get(1).getFortPos().getY());
			
			logger.info("second players fort pos before : " + players.get(0).getHalfMap().getFortPos().getX() + " " + players.get(0).getHalfMap().getFortPos().getY());*/
			transformCoordinates(players.get(0), halfMap1);
			
			
			//logger.info("second players pos AFTER: " + players.get(0).getTreasurePos().getX() + " " + players.get(0).getTreasurePos().getY());
			//logger.info("second players fort pos AFTER: " + players.get(0).getFortPos().getX() + " " + players.get(0).getFortPos().getY());
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
	
	private void printMapField(MapNode myMapField) {
		
		System.out.print(myMapField.getPosition().getX());
		System.out.print(myMapField.getPosition().getY());

		if(myMapField.getFieldType() == MapFieldType.GRASS) {
			System.out.print("G    ");
		}
		if(myMapField.getFieldType() == MapFieldType.MOUNTAIN) {
			System.out.print("M    ");			
		}
		if(myMapField.getFieldType() == MapFieldType.WATER) {
			System.out.print("W    ");
		}
		
	}
 	
	public void printMap() {
		

		for (int y = 0; y < this.ySize; y++) {
		
			for(int x =0; x < this.xSize; x++) {
				Coordinates pos = new Coordinates(x, y);
				printMapField(fields.get(pos));

				
			}
		
				System.out.println("\n");
		
			
		}
	}
	
	
}
