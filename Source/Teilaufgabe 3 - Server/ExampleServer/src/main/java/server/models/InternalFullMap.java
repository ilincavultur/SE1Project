package server.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromServer.FullMapNode;
import server.enums.MapFieldType;

public class InternalFullMap {
	private Map<Coordinates, MapNode> fields = new HashMap<Coordinates, MapNode>();
	private int xSize;
	private int ySize;
	

	
	public InternalFullMap() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Map<Coordinates, MapNode> getFields() {
		return fields;
	}


	public void setFields(Map<Coordinates, MapNode> fields) {
		this.fields = fields;
	}
	
	public void pickDimensions() {
		Random randomNo = new Random();
		int no = randomNo.nextInt(1);
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
		int no = randomNo.nextInt(1);
		if (no == 0) {
			return "first";
		}
		return "second";
		
	}
	
	public void assembleFullMap(List<Player> players, InternalHalfMap halfMap1, InternalHalfMap halfMap2) {
		pickDimensions();
		
		String first = pickFirstHalf();
		
		if (first.equals("first")) {
			fields.putAll(halfMap1.getFields());
			transformCoordinates(halfMap2);
		} else {
			fields.putAll(halfMap2.getFields());
			transformCoordinates(halfMap1);
		}
	}
	
	public void transformCoordinates(InternalHalfMap halfMap) {
		// square
		if (this.xSize == 8) {
		// y + 4
			for( Map.Entry<Coordinates, MapNode> mapEntry : halfMap.getFields().entrySet() ) {
				Coordinates newPos = new Coordinates(mapEntry.getKey().getX(), mapEntry.getKey().getY() + 4);
				mapEntry.getValue().setPosition(newPos);
				fields.put(newPos, mapEntry.getValue());
			}
		} else if (this.ySize == 16) {
		// x + 8
			for( Map.Entry<Coordinates, MapNode> mapEntry : halfMap.getFields().entrySet() ) {
				Coordinates newPos = new Coordinates(mapEntry.getKey().getX() + 8, mapEntry.getKey().getY());
				mapEntry.getValue().setPosition(newPos);
				fields.put(newPos, mapEntry.getValue());
			}
		}
	}
	
	private void printMapField(MapNode myMapField) {
		
		System.out.print(myMapField.getPosition().getX());
		System.out.print(myMapField.getPosition().getY());
		
		/*if (myMapField.getTreasureState() == TreasureState.MYTREASURE) {
			System.out.print("!!!T!!!");
		}
		
		if (myMapField.getFortState() == FortState.ENEMYFORT) {
			System.out.print("!!!EF!!!");
		}
		
		if(myMapField.getPlayerPositionState() == PlayerPositionState.BOTH || myMapField.getPlayerPositionState() == PlayerPositionState.MYPLAYER) {
			System.out.print("|A|");
		}
		
		if(myMapField.getPlayerPositionState() == PlayerPositionState.BOTH || myMapField.getPlayerPositionState() == PlayerPositionState.ENEMYPLAYER) {
			System.out.print("E");
		}*/
		
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
