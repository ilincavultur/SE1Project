package client.models.mapData;

import java.util.HashMap;
import java.util.Map;

public class Coordinates {
	int X;
	
	int Y;

	public Coordinates(int x, int y) {
		super();
		X = x;
		Y = y;
	}
	
	
	public int getX() {
		return X;
	}


	public void setX(int x) {
		X = x;
	}


	public int getY() {
		return Y;
	}


	public void setY(int y) {
		Y = y;
	}


	/*public Map<Coordinates, MapField> getFieldsAround( MapField field ) {
		
		Map<Coordinates, MapField> toReturn = new HashMap();
		
		//if (field.getFieldType() == MapFieldType.Mountain)
		//if berg:
		//toReturn.put(null, field)
		
		return null;	
	}*/
	
}
