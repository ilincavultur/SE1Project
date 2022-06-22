package server.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import MessagesBase.MessagesFromClient.HalfMapNode;
import server.enums.FortState;
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

	public Coordinates getFortPos() {
		return fortPos;
	}

	public void setFortPos(Coordinates fortPos) {
		this.fortPos = fortPos;
	}
	
	public Coordinates getTreasurePos() {
		for( Entry<Coordinates, MapNode> mapEntry : this.fields.entrySet() ) {
			if (mapEntry.getValue().getTreasureState() == TreasureState.MYTREASURE) {
				return mapEntry.getKey();
				
			}
		
		}
		return null;
	}
	
}
