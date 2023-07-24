package server.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import MessagesBase.MessagesFromClient.HalfMapNode;
import server.enums.FortState;
import server.enums.MapFieldType;
import server.enums.TreasureState;

public class InternalHalfMap {

	private Map<Coordinates, MapNode> fields = new HashMap<Coordinates, MapNode>();
	private Coordinates fortPos = new Coordinates();
	
	public InternalHalfMap() {
		super();
	}

	public Map<Coordinates, MapNode> getFields() {
		return fields;
	}

	public void setFields(Map<Coordinates, MapNode> fields) {
		this.fields = fields;
	}

	public Coordinates getFortPosition() {
		return fortPos;
	}

	public void setFortPos(Coordinates fortPos) {
		this.fortPos = fortPos;
	}
	
	public Coordinates placeTreasure() {
		
		Map<Coordinates, MapNode> fields = this.fields;
		
		Random randomNo = new Random();
		
		int randomFortX = randomNo.nextInt(8);
		int randomFortY = randomNo.nextInt(4);
		
		Coordinates treasurePosition = new Coordinates(randomFortX, randomFortY);
		
		while(fields.get(treasurePosition).getFieldType() != MapFieldType.GRASS) {
			randomFortX = randomNo.nextInt(8);
			randomFortY = randomNo.nextInt(4);
			treasurePosition = new Coordinates(randomFortX, randomFortY);
		}
		
		return treasurePosition;
		
	}
	
	public Coordinates getTreasurePosition() {
		for( var eachNode : this.fields.entrySet() ) {
			if (eachNode.getValue().getTreasureState() == TreasureState.MYTREASURE) {
				return eachNode.getKey();	
			}
		}
		return new Coordinates(-1,-1);
	}
	
}
