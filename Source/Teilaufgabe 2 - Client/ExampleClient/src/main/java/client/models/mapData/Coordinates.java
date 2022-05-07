package client.models.mapData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import client.models.mapData.enums.MapFieldType;

public class Coordinates {
	private int X;
	
	private int Y;

	public Coordinates(int x, int y) {
		super();
		X = x;
		Y = y;
	}
	
	public Coordinates(Coordinates coords) {
		super();
		X = coords.X;
		Y = coords.Y;
	}
	
	public Coordinates() {
		super();
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
	
	//TODO check if it s still in the map ??
	
	public Coordinates getUpNeighbour() {
		
		Coordinates neighbour = new Coordinates(this.X, this.Y-1);
				
		return neighbour;
		
	}
	
	public Coordinates getDownNeighbour() {
		
		Coordinates neighbour = new Coordinates(this.X, this.Y+1);
		
		return neighbour;
		
	}
	
	public Coordinates getLeftNeighbour() {
		
		Coordinates neighbour = new Coordinates(this.X-1, this.Y);
		
		return neighbour;
		
	}
	
	public Coordinates getRightNeighbour() {
		
		Coordinates neighbour = new Coordinates(this.X+1, this.Y);
		
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
	
	public Map<String, Coordinates> getFieldsAround(ClientMap myMap) {
		
		Map<String, Coordinates> toReturn = new HashMap<String, Coordinates>();
		
		if (myMap.getFields().containsKey(this.getUpNeighbour())) {
			if (myMap.getFields().get(this.getUpNeighbour()).getType() != MapFieldType.WATER) {
				toReturn.put("up", this.getUpNeighbour());	
			}
			
		}
		
		if (myMap.getFields().containsKey(this.getDownNeighbour())) {
			if (myMap.getFields().get(this.getDownNeighbour()).getType() != MapFieldType.WATER) {
				toReturn.put("down", this.getDownNeighbour());	
			}
			
		}
		
		if (myMap.getFields().containsKey(this.getLeftNeighbour())) {
			if (myMap.getFields().get(this.getLeftNeighbour()).getType() != MapFieldType.WATER) {
				toReturn.put("left", this.getLeftNeighbour());		
			}
		
		}
		
		if (myMap.getFields().containsKey(this.getRightNeighbour())) {
			if (myMap.getFields().get(this.getRightNeighbour()).getType() != MapFieldType.WATER) {
				toReturn.put("right", this.getRightNeighbour());	
			}
			
		}
		
		return toReturn;	
	}

	
}
