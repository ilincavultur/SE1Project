package client.models.mapData;

import java.util.Map;

public class ClientMap {
	
	Map<Coordinates, MapField> fields;
	
	int xSize;

	int ySize;

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
