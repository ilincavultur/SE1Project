package server.map;

import java.util.Map;
import java.util.Objects;

import MessagesBase.MessagesFromClient.HalfMapNode;
import server.enums.MoveCommand;

public class Coordinates {

	private int X;
	private int Y;
	
	public Coordinates(int x, int y) {
		super();
		X = x;
		Y = y;
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
	
	public Coordinates getUpNeighbour(Map<Coordinates, MapNode> fields) {
		
		Coordinates neighbour = new Coordinates(this.X, this.Y-1);
		
		if (fields.containsKey(neighbour)) {
			return neighbour;
		}
				
		return new Coordinates(-1,-1);
		
	}
	
	public Coordinates getNorthWestNeighbour(Map<Coordinates, MapNode> fields) {
		
		Coordinates neighbour = new Coordinates(this.X-1, this.Y-1);
		
		if (fields.containsKey(neighbour)) {
			return neighbour;
		}
				
		return new Coordinates(-1,-1);
		
	}

	public Coordinates getDownNeighbour(Map<Coordinates, MapNode> fields) {
		
		Coordinates neighbour = new Coordinates(this.X, this.Y+1);
		
		if (fields.containsKey(neighbour)) {
			return neighbour;
		}
				
		return new Coordinates(-1,-1);
		
	}
	
	public Coordinates getNorthEastNeighbour(Map<Coordinates, MapNode> fields) {
		
		Coordinates neighbour = new Coordinates(this.X+1, this.Y-1);
		
		if (fields.containsKey(neighbour)) {
			return neighbour;
		}
				
		return new Coordinates(-1,-1);
		
	}
	
	public Coordinates getLeftNeighbour(Map<Coordinates, MapNode> fields) {
		
		Coordinates neighbour = new Coordinates(this.X-1, this.Y);
		
		if (fields.containsKey(neighbour)) {
			return neighbour;
		}
				
		return new Coordinates(-1,-1);
		
	}
	
	public Coordinates getSouthEastNeighbour(Map<Coordinates, MapNode> fields) {
		
		Coordinates neighbour = new Coordinates(this.X+1, this.Y+1);
		
		if (fields.containsKey(neighbour)) {
			return neighbour;
		}
				
		return new Coordinates(-1,-1);
		
	}
	
	public Coordinates getSouthWestNeighbour(Map<Coordinates, MapNode> fields) {
		
		Coordinates neighbour = new Coordinates(this.X-1, this.Y+1);
		
		if (fields.containsKey(neighbour)) {
			return neighbour;
		}
				
		return new Coordinates(-1,-1);
		
	}
	
	public Coordinates getRightNeighbour(Map<Coordinates, MapNode> fields) {
		
		Coordinates neighbour = new Coordinates(this.X+1, this.Y);
		
		if (fields.containsKey(neighbour)) {
			return neighbour;
		}
				
		return new Coordinates(-1,-1);
		
	}
	
	public boolean isCoordinateValid() {
		return (this.X >= 0 && this.Y >= 0);
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
	
}
