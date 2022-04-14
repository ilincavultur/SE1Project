package client.models.mapData;

import java.util.Map;

public class ClientMap {
	
	Map<Coordinates, MapField> fields;
	
	String gameID;
	
	int xSize;

	int ySize;
	
	
	
	public ClientMap() {
		super();
	}
	
	
	
	public ClientMap(Map<Coordinates, MapField> fields) {
		super();
		this.fields = fields;
	}



	public ClientMap(ClientMap myMap) {
		this.fields = myMap.fields;
		//this.xSize = myMap.xSize;
		//this.ySize = myMap.ySize;
	}

	public ClientMap(Map<Coordinates, MapField> fields, String gameID, int xSize, int ySize) {
		super();
		this.fields = fields;
		this.gameID = gameID;
		this.xSize = xSize;
		this.ySize = ySize;
	}

	public Map<Coordinates, MapField> getFields() {
		return fields;
	}

	public void setFields(Map<Coordinates, MapField> fields) {
		this.fields = fields;
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
	
	
	
}
