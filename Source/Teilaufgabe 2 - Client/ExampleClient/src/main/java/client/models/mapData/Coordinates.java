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

	public Coordinates getUpNeighbour(ClientMap myMap) {
		
		Coordinates neighbour = new Coordinates(this.X, this.Y-1);
		
		if (myMap.getFields().containsKey(neighbour)) {
			return neighbour;
		}
				
		return null;
		
	}
	
	public Coordinates getNorthWestNeighbour(ClientMap myMap) {
		
		Coordinates neighbour = new Coordinates(this.X-1, this.Y-1);
		
		if (myMap.getFields().containsKey(neighbour)) {
			return neighbour;
		}
				
		return null;
		
	}
	
	public Coordinates getDownNeighbour(ClientMap myMap) {
		
		Coordinates neighbour = new Coordinates(this.X, this.Y+1);
		
		if (myMap.getFields().containsKey(neighbour)) {
			return neighbour;
		}
				
		return null;
		
	}
	
	public Coordinates getNorthEastNeighbour(ClientMap myMap) {
		
		Coordinates neighbour = new Coordinates(this.X+1, this.Y-1);
		
		if (myMap.getFields().containsKey(neighbour)) {
			return neighbour;
		}
				
		return null;
		
	}
	
	public Coordinates getLeftNeighbour(ClientMap myMap) {
		
		Coordinates neighbour = new Coordinates(this.X-1, this.Y);
		
		if (myMap.getFields().containsKey(neighbour)) {
			return neighbour;
		}
				
		return null;
		
	}
	
	public Coordinates getSouthEastNeighbour(ClientMap myMap) {
		
		Coordinates neighbour = new Coordinates(this.X+1, this.Y+1);
		
		if (myMap.getFields().containsKey(neighbour)) {
			return neighbour;
		}
				
		return null;
		
	}
	
	public Coordinates getRightNeighbour(ClientMap myMap) {
		
		Coordinates neighbour = new Coordinates(this.X+1, this.Y);
		
		if (myMap.getFields().containsKey(neighbour)) {
			return neighbour;
		}
				
		return null;
		
	}
	
	public Coordinates getSouthWestNeighbour(ClientMap myMap) {
		
		Coordinates neighbour = new Coordinates(this.X-1, this.Y+1);
		
		if (myMap.getFields().containsKey(neighbour)) {
			return neighbour;
		}
				
		return null;
		
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
		
		if (myMap.getFields().get(this.getUpNeighbour(myMap)) != null) {
			if (myMap.getFields().get(this.getUpNeighbour(myMap)).getType() != MapFieldType.WATER) {
				
					toReturn.put("up", this.getUpNeighbour(myMap));		
				
			}
		}
		
		if (myMap.getFields().get(this.getDownNeighbour(myMap)) != null) {
			if (myMap.getFields().get(this.getDownNeighbour(myMap)).getType() != MapFieldType.WATER) {
				
					toReturn.put("down", this.getDownNeighbour(myMap));	
				
					
			}
		}
		
		if (myMap.getFields().get(this.getLeftNeighbour(myMap)) != null) {
			if (myMap.getFields().get(this.getLeftNeighbour(myMap)).getType() != MapFieldType.WATER) {
				
					toReturn.put("left", this.getLeftNeighbour(myMap));	
				
						
			}
		}
	
		if (myMap.getFields().get(this.getRightNeighbour(myMap)) != null) {
			if (myMap.getFields().get(this.getRightNeighbour(myMap)).getType() != MapFieldType.WATER) {
				
					toReturn.put("right", this.getRightNeighbour(myMap));	
					
			}
		}
		
		
		return toReturn;	
	}
	
	/*
	public Map<String, Coordinates> getFieldsAround(ClientMap myMap, Map<Coordinates, MapField> unvisitedTotal) {
		
		Map<String, Coordinates> toReturn = new HashMap<String, Coordinates>();
		
		if (myMap.getFields().get(this.getUpNeighbour(myMap)) != null) {
			if (myMap.getFields().get(this.getUpNeighbour(myMap)).getType() != MapFieldType.WATER) {
				if (unvisitedTotal.containsKey(this.getUpNeighbour(myMap))) {
					toReturn.put("up", this.getUpNeighbour(myMap));		
				}
			}
		}
		
		if (myMap.getFields().get(this.getDownNeighbour(myMap)) != null) {
			if (myMap.getFields().get(this.getDownNeighbour(myMap)).getType() != MapFieldType.WATER) {
				if (unvisitedTotal.containsKey(this.getDownNeighbour(myMap))) {
					toReturn.put("down", this.getDownNeighbour(myMap));	
				}
					
			}
		}
		
		if (myMap.getFields().get(this.getLeftNeighbour(myMap)) != null) {
			if (myMap.getFields().get(this.getLeftNeighbour(myMap)).getType() != MapFieldType.WATER) {
				if (unvisitedTotal.containsKey(this.getLeftNeighbour(myMap)) ) {
					toReturn.put("left", this.getLeftNeighbour(myMap));	
				}
						
			}
		}
	
		if (myMap.getFields().get(this.getRightNeighbour(myMap)) != null) {
			if (myMap.getFields().get(this.getRightNeighbour(myMap)).getType() != MapFieldType.WATER) {
				if (unvisitedTotal.containsKey(this.getRightNeighbour(myMap))) {
					toReturn.put("right", this.getRightNeighbour(myMap));	
				}
					
			}
		}
		
		
		return toReturn;	
	}
	*/
	public Map<String, Coordinates> getFieldsAroundMountain(ClientMap myMap) {
		
		Map<String, Coordinates> toReturn = new HashMap<String, Coordinates>();
		
		if (myMap.getFields().get(this.getUpNeighbour(myMap)) != null) {
			if (myMap.getFields().get(this.getUpNeighbour(myMap)).getType() != MapFieldType.WATER) {
				toReturn.put("up", this.getUpNeighbour(myMap));	
			}
		}
		
		if (myMap.getFields().get(this.getNorthWestNeighbour(myMap)) != null) {
			if (myMap.getFields().get(this.getNorthWestNeighbour(myMap)).getType() != MapFieldType.WATER) {
				toReturn.put("nw", this.getNorthWestNeighbour(myMap));	
			}
		}
		
		if (myMap.getFields().get(this.getDownNeighbour(myMap)) != null) {
			if (myMap.getFields().get(this.getDownNeighbour(myMap)).getType() != MapFieldType.WATER) {
				toReturn.put("down", this.getDownNeighbour(myMap));	
			}
		}
		
		if (myMap.getFields().get(this.getNorthEastNeighbour(myMap)) != null) {
			if (myMap.getFields().get(this.getNorthEastNeighbour(myMap)).getType() != MapFieldType.WATER) {
				toReturn.put("ne", this.getNorthEastNeighbour(myMap));	
			}
		}
		
		if (myMap.getFields().get(this.getLeftNeighbour(myMap)) != null) {
			if (myMap.getFields().get(this.getLeftNeighbour(myMap)).getType() != MapFieldType.WATER) {
				toReturn.put("left", this.getLeftNeighbour(myMap));		
			}
		}
		
		if (myMap.getFields().get(this.getSouthEastNeighbour(myMap)) != null) {
			if (myMap.getFields().get(this.getSouthEastNeighbour(myMap)).getType() != MapFieldType.WATER) {
				toReturn.put("se", this.getSouthEastNeighbour(myMap));	
			}
		}
	
		if (myMap.getFields().get(this.getRightNeighbour(myMap)) != null) {
			if (myMap.getFields().get(this.getRightNeighbour(myMap)).getType() != MapFieldType.WATER) {
				toReturn.put("right", this.getRightNeighbour(myMap));	
			}
		}
		
		if (myMap.getFields().get(this.getSouthWestNeighbour(myMap)) != null) {
			if (myMap.getFields().get(this.getSouthWestNeighbour(myMap)).getType() != MapFieldType.WATER) {
				toReturn.put("sw", this.getSouthWestNeighbour(myMap));	
			}
		}
		
		
		return toReturn;	
	}

	
}
