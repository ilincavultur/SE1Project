package client.models.mapData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import client.models.mapData.enums.MapFieldType;

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
	
	public Coordinates getUpNeighbour(Coordinates field) {
		
		Coordinates neighbour = new Coordinates(field.X, field.Y+1);
		
		return neighbour;
		
	}
	
	public Coordinates getDownNeighbour(Coordinates field) {
		
		Coordinates neighbour = new Coordinates(field.X, field.Y-1);
		
		return neighbour;
		
	}
	
	public Coordinates getLeftNeighbour(Coordinates field) {
		
		Coordinates neighbour = new Coordinates(field.X-1, field.Y);
		
		return neighbour;
		
	}
	
	public Coordinates getRightNeighbour(Coordinates field) {
		
		Coordinates neighbour = new Coordinates(field.X+1, field.Y);
		
		return neighbour;
		
	}


	@Override
	public int hashCode() {
		return Objects.hash(X, Y);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinates other = (Coordinates) obj;
		return X == other.X && Y == other.Y;
	}

	

	/*public Map<Coordinates, MapField> getFieldsAround( MapField field ) {
		
		Map<Coordinates, MapField> toReturn = new HashMap();
		
		Coordinates pos = field.getPosition();
		
		if (field.getType() == MapFieldType.MOUNTAIN) {
			for (int i = 0; i < 7; i++) {
				MapField nodeToAdd = new MapField();
					
				
				
			}
		}
		
		
		//if (field.getFieldType() == MapFieldType.Mountain)
		//if berg:
		//toReturn.put(null, field)
		
		
		return null;	
	}*/
	
}
