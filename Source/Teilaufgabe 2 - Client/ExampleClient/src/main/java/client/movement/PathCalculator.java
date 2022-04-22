package client.movement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.models.mapData.enums.MapFieldType;

public class PathCalculator {
	
	private ClientMap myMap;
	
	
	
	public ClientMap getMyMap() {
		return myMap;
	}

	public void setMyMap(ClientMap myMap) {
		this.myMap = myMap;
	}

	private int getPathWeight(Coordinates currentField, Coordinates nextField) {
		
		return myMap.getFields().get(currentField).getMoves() + myMap.getFields().get(nextField).getMoves();
		
	}
	
	private Map<String, Coordinates> neighboursWithoutWaters (Map<String, Coordinates> neighbours) {
		
		Map<String, Coordinates> toReturn = new HashMap<String, Coordinates>();
		
		for( Map.Entry<String, Coordinates> mapEntry : neighbours.entrySet() ) {
			
			if (myMap.getFields().get(mapEntry.getValue()).getType() != MapFieldType.WATER) {
				toReturn.put(mapEntry.getKey(), mapEntry.getValue());
			}
		}
		
		return toReturn;
		
	}

	public List<Coordinates> getShortestPath(MapField startingField, MapField targetField) {
		
		Coordinates startingPos = startingField.getPosition();
		Coordinates targetPos = targetField.getPosition();
		Coordinates nextPos = targetField.getPosition();
		Integer pathValue = 0;
		List<Coordinates> visitedFields = new ArrayList<Coordinates>();
		List<Coordinates> unvisitedFields = new ArrayList<Coordinates>();
		
		for( Map.Entry<Coordinates, MapField> mapEntry : myMap.getFields().entrySet() ) {
			unvisitedFields.add(mapEntry.getKey());
		}
		
		// prev Vertex , path to it from startingField
		Map<Coordinates, Integer> shortestPath = new HashMap<Coordinates, Integer>();
		
		
		for( Map.Entry<Coordinates, MapField> mapEntry : myMap.getFields().entrySet() ) {
			shortestPath.put(mapEntry.getKey(), 99999999);	
		}
		
		visitedFields.add(startingPos);
		unvisitedFields.remove(startingPos);
		shortestPath.put(startingPos, 0);
		
		while (!visitedFields.contains(targetPos) && unvisitedFields.contains(startingPos)) {
			Map<String, Coordinates> neighbours = neighboursWithoutWaters(startingPos.getFieldsAround(myMap));

			for (int i=0; i<neighbours.size(); i++) {
				nextPos = getNextPos(startingPos, neighbours);
				pathValue = getPathWeight(startingPos, nextPos);
				if (shortestPath.get(nextPos) > pathValue) {
					shortestPath.replace(nextPos, pathValue);
				}
				visitedFields.add(nextPos);
				unvisitedFields.remove(nextPos);
				startingPos = nextPos;
			}
		}
		
		/*List<Coordinates> toReturn = new ArrayList<Coordinates>();
 		
		
		Map<Coordinates, Integer> visitedFields = new HashMap<Coordinates, Integer>();
		
		//all nodes are unvisited
		Map<Coordinates, MapField> unvisitedFields = myMap.getFields();
		for( Map.Entry<Coordinates, Integer> mapEntry : visitedFields.entrySet() ) {
			// initialise with a kind of infinity
			mapEntry.setValue(999999999);
		}
		
		Coordinates startingPos = startingField.getPosition();
		Coordinates targetPos = targetField.getPosition();
		
		visitedFields.put(startingPos, 0);
		
		int weightUp = 0;
		int weightDown = 0;
		int weightLeft = 0;
		int weightRight = 0;
		int minValue = 0;
		
		while (!visitedFields.containsKey(targetPos) && unvisitedFields.containsKey(startingPos)) {
			
			Map<String, Coordinates> neighbours = neighboursWithoutWaters(startingPos.getFieldsAround(myMap));
			
			
			for (int i=0; i<neighbours.size(); i++) {
				List<Integer> values = new ArrayList<Integer>();
				//TODO get next node 
				weightUp = getPathWeight(myMap.getFields().get(startingPos), myMap.getFields().get(neighbours.get("up")));
				weightDown = getPathWeight(myMap.getFields().get(startingPos), myMap.getFields().get(neighbours.get("down")));
				weightLeft = getPathWeight(myMap.getFields().get(startingPos), myMap.getFields().get(neighbours.get("left")));
				weightRight = getPathWeight(myMap.getFields().get(startingPos), myMap.getFields().get(neighbours.get("right")));
				values.add(weightUp);
				values.add(weightDown);
				values.add(weightLeft);
				values.add(weightRight);
				minValue = Collections.min(values);
				if (minValue == weightUp) {
					startingPos = myMap.getFields().get(neighbours.get("up")).getPosition();
				} else if (minValue == weightDown) {
					startingPos = myMap.getFields().get(neighbours.get("down")).getPosition();
				} else if (minValue == weightLeft) {
					startingPos = myMap.getFields().get(neighbours.get("left")).getPosition();
				} else if (minValue == weightRight) {
					startingPos = myMap.getFields().get(neighbours.get("right")).getPosition();
				}
				if (visitedFields.get(startingPos) < minValue) {
					visitedFields.replace(startingPos, minValue);
				}
				unvisitedFields.remove(startingPos);
				
			}
		}
		
		
		return toReturn;
		*/
		return visitedFields;
	}
	
	private Coordinates getNextPos(Coordinates currPos, Map<String, Coordinates> neighbours) {
		Coordinates toReturn = new Coordinates();
		
		int weightUp = 0;
		int weightDown = 0;
		int weightLeft = 0;
		int weightRight = 0;
		int minValue = 10;
		List<Integer> values = new ArrayList<Integer>();
		
		//TODO get next node 
		if ( neighbours.containsKey("up")) {
			weightUp = getPathWeight(currPos, neighbours.get("up"));	
			values.add(weightUp);
		}
		if ( neighbours.containsKey("down")) {
			weightDown = getPathWeight(currPos, neighbours.get("down"));			
			values.add(weightDown);
		}
		if ( neighbours.containsKey("left")) {
			weightLeft = getPathWeight(currPos, neighbours.get("left"));
			values.add(weightLeft);		
		}
		if ( neighbours.containsKey("right")) {
			weightRight = getPathWeight(currPos, neighbours.get("right"));
			values.add(weightRight);
		}
		
		minValue = Collections.min(values);
		if (minValue == weightUp) {
			toReturn = neighbours.get("up");
		} else if (minValue == weightDown) {
			toReturn = neighbours.get("down");
		} else if (minValue == weightLeft) {
			toReturn = neighbours.get("left");
		} else if (minValue == weightRight) {
			toReturn = neighbours.get("right");
		}
		
		return toReturn;
	}
}

