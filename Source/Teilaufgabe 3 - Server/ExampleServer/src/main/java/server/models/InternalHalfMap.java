package server.models;

import java.util.HashMap;
import java.util.Map;

import MessagesBase.MessagesFromClient.HalfMapNode;

public class InternalHalfMap {

	private Map<Coordinates, MapNode> fields = new HashMap<Coordinates, MapNode>();
	private Coordinates fortPos = new Coordinates();
	
	public InternalHalfMap() {
		super();
		// TODO Auto-generated constructor stub
	}


	public InternalHalfMap(Map<Coordinates, MapNode> fields) {
		super();
		this.fields = fields;
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
	
	
	
}
