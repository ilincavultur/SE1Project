package client.movement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.models.mapData.enums.MapFieldType;
import client.movement.enums.MoveCommand;

public class PathCalculator {
	
	private ClientMap myMap;
	Coordinates startPos;
	/*Map<Coordinates, Integer> costs = new HashMap<Coordinates, Integer>();
	List<Coordinates> visitedFields = new ArrayList<Coordinates>();
	// the value is the current Node, the key is the previous node
	Map<Coordinates, Coordinates> previousNode = new HashMap<Coordinates, Coordinates>();
	Map<Coordinates, MapField> unvisitedFields = new HashMap<Coordinates, MapField>();*/
	
	Map<Coordinates, Integer> costs = new HashMap<Coordinates, Integer>();
	// the value is the current Node, the key is the previous node
	List<Coordinates> settledNodes = new ArrayList<Coordinates>();
	List<Coordinates> unsettledNodes = new ArrayList<Coordinates>();
	Map<Coordinates, Coordinates> previousNode = new HashMap<Coordinates, Coordinates>();
	private static final Logger logger = LoggerFactory.getLogger(PathCalculator.class);
	
	public PathCalculator(ClientMap myMap) {
		super();
		this.myMap = myMap;
	}
	
	public PathCalculator() {
		super();
	}

	public ClientMap getMyMap() {
		return myMap;
	}

	public void setMyMap(ClientMap myMap) {
		this.myMap = myMap;
	}

	private int getPathWeight(Coordinates currentField, Coordinates nextField) {
		
 		System.out.println(myMap.getFields().get(currentField).getMoves());
		return myMap.getFields().get(currentField).getMoves() + myMap.getFields().get(nextField).getMoves();
		
	}
	
	
	
	public Map<Coordinates, Integer> getCosts() {
		return costs;
	}

	public void setCosts(Map<Coordinates, Integer> costs) {
		this.costs = costs;
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

	public void getShortestPath(MapField startingField, MapField targetField) {
		Coordinates currPos = new Coordinates();
		this.startPos = startingField.getPosition();
		Map<Coordinates, MapField> visitableFields = myMap.getVisitableNodes();
		this.previousNode = new HashMap<Coordinates, Coordinates>();
		// initialise costs
		for( Map.Entry<Coordinates, MapField> mapEntry : visitableFields.entrySet() ) {
			costs.put(mapEntry.getKey(), 99999999);	
			//previousNode.put(mapEntry.getKey(), null);
		}
		costs.put(startingField.getPosition(), 0);
		unsettledNodes.add(startingField.getPosition());
		
		while (unsettledNodes.size() != 0 ) {
			currPos = getLowestDistanceNode(unsettledNodes);
			
			unsettledNodes.remove(currPos);
			// luam vecinii nodului curent
			Map<String, Coordinates> neighbours = neighboursWithoutWaters(currPos.getFieldsAround(myMap));
			for( Entry<String, Coordinates> mapEntry : neighbours.entrySet() ) {
				if (!settledNodes.contains(mapEntry.getValue())) {
					
					calcMinDistance(currPos, mapEntry);	
					
					
					unsettledNodes.add(mapEntry.getValue());

					
					
				}
			}
			settledNodes.add(currPos);
			
			if(currPos.equals(targetField.getPosition())) {
				targetField.getShortestPath().add(currPos);
				logger.info("am ajuns la target");
				break;
			}
		}
		
		/*for( Map.Entry<Coordinates, Integer> mapEntry : costs.entrySet() ) {
			System.out.print(mapEntry.getKey().getX() + " ");
			System.out.print(mapEntry.getKey().getY() + " ");
			System.out.print(mapEntry.getValue());
			System.out.println();
		}*/
		
		logger.info("pathulllll");
		for (int i=0; i<settledNodes.size(); i++) {
			if(myMap.getFields().get(settledNodes.get(i)).getPosition().equals(targetField.getPosition())) {
				System.out.println("path for" + myMap.getFields().get(settledNodes.get(i)).getPosition().getX() + " " + myMap.getFields().get(settledNodes.get(i)).getPosition().getY());
				List<Coordinates> sPath = myMap.getFields().get(settledNodes.get(i)).getShortestPath();
				for (int j=0; j<sPath.size(); j++) {
					
					System.out.println (sPath.get(j).getX() + " " + sPath.get(j).getY());
				}
			}
			

		}
		
		for( Map.Entry<Coordinates, Coordinates> mapEntry : previousNode.entrySet() ) {
			System.out.print("prev");
			System.out.print(mapEntry.getKey().getX() + " ");
			System.out.print(mapEntry.getKey().getY() + " ");
			System.out.print("curr ");
			System.out.print(mapEntry.getValue().getX() + " ");
			System.out.print(mapEntry.getValue().getY() + " ");
			System.out.println();
		}
		
	}
	
	public List<MoveCommand> getMovesPath(MapField field) {
		// get it from the target field node
		List<MoveCommand> toReturn = new ArrayList<MoveCommand>();
		List<Coordinates> sPath = field.getShortestPath();
		
		/*for (int i=0; i<sPath.size(); i++) {
			System.out.println("AICIIIII S PATH");
			System.out.println (sPath.get(i).getX() + " " + sPath.get(i).getY());
	
		}*/
		Coordinates stPos = this.startPos;
		for (int i=0; i<sPath.size(); i++) {
			//asta nuj daca ii bine
			System.out.println (sPath.get(i).getX() + " " + sPath.get(i).getY());
			if (myMap.getFields().get(sPath.get(i)).getPosition().equals(stPos.getUpNeighbour()) ) {
				int movesNo = myMap.getFields().get(stPos).getMoves() + myMap.getFields().get(stPos.getUpNeighbour()).getMoves();
				//System.out.println(movesNo);
				stPos = myMap.getFields().get(sPath.get(i)).getPosition();
				for (int j = 0; j < movesNo; j++) {
					toReturn.add(MoveCommand.UP);
				}
				
			}
			if (myMap.getFields().get(sPath.get(i)).getPosition().equals(stPos.getDownNeighbour()) ) {
				int movesNo = myMap.getFields().get(stPos).getMoves() + myMap.getFields().get(stPos.getDownNeighbour()).getMoves();
				//System.out.println(movesNo);
				stPos = myMap.getFields().get(sPath.get(i)).getPosition();
				for (int j = 0; j < movesNo; j++) {
					toReturn.add(MoveCommand.DOWN);
				}
				
			}
			if (myMap.getFields().get(sPath.get(i)).getPosition().equals(stPos.getLeftNeighbour()) ) {
				int movesNo = myMap.getFields().get(stPos).getMoves() + myMap.getFields().get(stPos.getLeftNeighbour()).getMoves();
				//System.out.println(movesNo);
				stPos = myMap.getFields().get(sPath.get(i)).getPosition();
				for (int j = 0; j < movesNo; j++) {
					toReturn.add(MoveCommand.LEFT);
				}
				
			}
			if (myMap.getFields().get(sPath.get(i)).getPosition().equals(stPos.getRightNeighbour())) {
				int movesNo = myMap.getFields().get(stPos).getMoves() + myMap.getFields().get(stPos.getRightNeighbour()).getMoves();
				//System.out.println(movesNo);
				stPos = myMap.getFields().get(sPath.get(i)).getPosition();
				for (int j = 0; j < movesNo; j++) {
					toReturn.add(MoveCommand.RIGHT);
				}
				
			}
			
			

		}
		/*for (int i=0; i<toReturn.size(); i++) {
			System.out.println("AICIIIII");
			System.out.println (toReturn.get(i).toString());
	
		}*/
		return toReturn;
	}
	
	public void calcMinDistance(Coordinates startingPos, Entry<String, Coordinates> mapEntry) {
		//System.out.println("CAUTA AICI" + startingPos.getX() + " " + startingPos.getY());
		int sourceDist = costs.get(startingPos);
		int weight = 0;
		
		if(mapEntry.getKey() == "up") {
			weight = getPathWeight(startingPos, mapEntry.getValue());
		}
		
		if(mapEntry.getKey() == "down") {
			weight = getPathWeight(startingPos, mapEntry.getValue());
			
		}
		
		if(mapEntry.getKey() == "left") {
			weight = getPathWeight(startingPos, mapEntry.getValue());
		
		}
		
		if(mapEntry.getKey() == "right") {
			weight = getPathWeight(startingPos, mapEntry.getValue());
		
		}
		if (sourceDist + weight < costs.get(mapEntry.getValue())) {
			
			costs.put(mapEntry.getValue(), sourceDist + weight);
			List<Coordinates> path = new ArrayList<Coordinates>(myMap.getFields().get(startingPos).getShortestPath());
			if(!startingPos.equals(this.startPos)) {
				path.add(startingPos);
			}
			//path.add(startingPos);
			previousNode.put(mapEntry.getValue(), startingPos);
			myMap.getFields().get(mapEntry.getValue()).setShortestPath(path);
			
		}
	}
	
	public Coordinates getLowestDistanceNode(List<Coordinates> nodes) {
		int min = 9999;
		Coordinates toReturn = new Coordinates();
		for (Coordinates coords : nodes) {
			if (costs.get(coords) < min) {
				min = costs.get(coords);
				toReturn = coords;
			}
		}
		return toReturn;
	}
	
	public void updateCosts(Coordinates startingPos, Map<String, Coordinates> neighbours) {
		int weight = 0;
		for( Map.Entry<String, Coordinates> mapEntry : neighbours.entrySet() ) {
			if(mapEntry.getKey() == "up") {
				weight = getPathWeight(startingPos, neighbours.get("up"));
				//System.out.println(mapEntry.getValue().getX() + " " + mapEntry.getValue().getY() + " weight" + weight);
				if (weight + costs.get(startingPos) < costs.get(neighbours.get("up"))) {
					costs.put(neighbours.get("up"), weight + costs.get(startingPos));
					//previousNode.put(startingPos, neighbours.get("up"));
				}
			}
			
			if(mapEntry.getKey() == "down") {
				weight = getPathWeight(startingPos, neighbours.get("down"));
				System.out.println(mapEntry.getValue().getX() + " " + mapEntry.getValue().getY() + " weight" + weight);
				if (weight + costs.get(startingPos) < costs.get(neighbours.get("down"))) {
					costs.put(neighbours.get("down"), weight + costs.get(startingPos));
					//previousNode.put(startingPos, neighbours.get("down"));
				}	
			}
			
			if(mapEntry.getKey() == "left") {
				weight = getPathWeight(startingPos, neighbours.get("left"));
				System.out.println(mapEntry.getValue().getX() + " " + mapEntry.getValue().getY() + " weight" + weight);
				if (weight + costs.get(startingPos) < costs.get(neighbours.get("left"))) {
					costs.put(neighbours.get("left"), weight + costs.get(startingPos));
					//previousNode.put(startingPos, neighbours.get("left"));
				}
			}
			
			if(mapEntry.getKey() == "right") {
				weight = getPathWeight(startingPos, neighbours.get("right"));
				System.out.println(mapEntry.getValue().getX() + " " + mapEntry.getValue().getY() + " weight" + weight);
				if (weight + costs.get(startingPos) < costs.get(neighbours.get("right"))) {
					costs.put(neighbours.get("right"), weight + costs.get(startingPos));
					//previousNode.put(startingPos, neighbours.get("right"));
				}
			}
		}
	}
		
	private Coordinates getNextPos(Coordinates currPos, Map<String, Coordinates> neighbours, Map<Coordinates, MapField> unvisited) {
		//List<Integer> values = new ArrayList<Integer>();
		
		int min = 99999;
		
		/*for( Entry<String, Coordinates> mapEntry : neighbours.entrySet() ) {
			System.out.println();
			System.out.println();
			System.out.print(mapEntry.getValue().getX() + " ");
			System.out.print(mapEntry.getValue().getY() + " ");
			System.out.println();
		}*/
		
		
		for( Map.Entry<String, Coordinates> mapEntry : neighbours.entrySet() ) {
			if (costs.get(mapEntry.getValue()) < min && unvisited.containsKey(mapEntry.getValue())) {
				min = costs.get(mapEntry.getValue());
				//System.out.println("min " + min); 
			}
			//values.add(costs.get(mapEntry.getValue()));
		}
		
		// get next minimum weight and also unvisited node
		for( Map.Entry<String, Coordinates> mapEntry : neighbours.entrySet() ) {
			if(costs.get(mapEntry.getValue()) == min && unvisited.containsKey(mapEntry.getValue())) {
				return mapEntry.getValue();
			}
		}
				
		return null;
	}
}

