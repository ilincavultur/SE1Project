package client.movement;

import java.util.ArrayList;
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
	Map<Coordinates, Integer> costs = new HashMap<Coordinates, Integer>();
	List<Coordinates> settledNodes = new ArrayList<Coordinates>();
	List<Coordinates> unsettledNodes = new ArrayList<Coordinates>();
	//the value is the current Node, the key is the previous node
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
	
	public int getMoves(MapField field) {
		if (field.getType() == MapFieldType.MOUNTAIN) {
			return 2;
		} 
		return 1;
	}

	private int getPathWeight(Coordinates currentField, Coordinates nextField) {
		
		int firstFieldMoves = this.getMoves(myMap.getFields().get(currentField)); 
		int secondFieldMoves = this.getMoves(myMap.getFields().get(nextField)); 
 		
		return firstFieldMoves + secondFieldMoves;
		
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

	//https://www.baeldung.com/java-dijkstra
	public void getShortestPath(Coordinates startingField, MapField targetField) {
		Coordinates currPos = new Coordinates();
		this.startPos = startingField;
		
		Map<Coordinates, MapField> visitableFields = myMap.getVisitableNodes();
		this.previousNode = new HashMap<Coordinates, Coordinates>();
		
		// initialise costs
		for( Map.Entry<Coordinates, MapField> mapEntry : visitableFields.entrySet() ) {
			costs.put(mapEntry.getKey(), 99999999);	
		}
		costs.put(startingField, 0);
		unsettledNodes.add(startingField);
		
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
				//TODO
				//targetField.getShortestPath().add(currPos);
				logger.info("am ajuns la target");
				break;
			}
		}
		
		targetField.setShortestPath(settledNodes);
		
		//------------------------- test print
		/*logger.info("pathulllll");
		for (int i=0; i<settledNodes.size(); i++) {
			if(myMap.getFields().get(settledNodes.get(i)).getPosition().equals(targetField.getPosition())) {
				System.out.println("path for" + myMap.getFields().get(settledNodes.get(i)).getPosition().getX() + " " + myMap.getFields().get(settledNodes.get(i)).getPosition().getY());
				List<Coordinates> sPath = myMap.getFields().get(settledNodes.get(i)).getShortestPath();
				for (int j=0; j<sPath.size(); j++) {
					
					System.out.println (sPath.get(j).getX() + " " + sPath.get(j).getY());
				}
			}
			

		}*/
		/*logger.info("aici");
		for( Map.Entry<Coordinates, Coordinates> mapEntry : previousNode.entrySet() ) {
			System.out.print("prev");
			System.out.print(mapEntry.getKey().getX() + " ");
			System.out.print(mapEntry.getKey().getY() + " ");
			System.out.print("curr ");
			System.out.print(mapEntry.getValue().getX() + " ");
			System.out.print(mapEntry.getValue().getY() + " ");
			System.out.println();
		}*/
		//------------------------- test print
		
	}
	
	public List<MoveCommand> getMovesPath(MapField field) {
		// get it from the target field node
		List<MoveCommand> toReturn = new ArrayList<MoveCommand>();
		List<Coordinates> sPath = field.getShortestPath();
		
		Coordinates stPos = this.startPos;
		for (int i=0; i<sPath.size(); i++) {
			
			//asta nuj daca ii bine
			
			if (myMap.getFields().get(sPath.get(i)).getPosition().equals(stPos.getUpNeighbour(myMap)) ) {
			
				int movesNo = this.getPathWeight(stPos,stPos.getUpNeighbour(myMap));
				
				stPos = myMap.getFields().get(sPath.get(i)).getPosition();
				for (int j = 0; j < movesNo; j++) {
					toReturn.add(MoveCommand.UP);
				}
				
			}
			if (myMap.getFields().get(sPath.get(i)).getPosition().equals(stPos.getDownNeighbour(myMap)) ) {
			
				int movesNo = this.getPathWeight(stPos,stPos.getDownNeighbour(myMap));
				
				stPos = myMap.getFields().get(sPath.get(i)).getPosition();
				for (int j = 0; j < movesNo; j++) {
					toReturn.add(MoveCommand.DOWN);
				}
				
			}
			if (myMap.getFields().get(sPath.get(i)).getPosition().equals(stPos.getLeftNeighbour(myMap)) ) {
				
				int movesNo = this.getPathWeight(stPos,stPos.getLeftNeighbour(myMap));
				
				stPos = myMap.getFields().get(sPath.get(i)).getPosition();
				for (int j = 0; j < movesNo; j++) {
					toReturn.add(MoveCommand.LEFT);
				}
				
			}
			if (myMap.getFields().get(sPath.get(i)).getPosition().equals(stPos.getRightNeighbour(myMap))) {
			
				int movesNo = this.getPathWeight(stPos,stPos.getRightNeighbour(myMap));
				
				stPos = myMap.getFields().get(sPath.get(i)).getPosition();
				for (int j = 0; j < movesNo; j++) {
					toReturn.add(MoveCommand.RIGHT);
				}
				
			}
			
			

		}
	
		return toReturn;
	}
	
	public MoveCommand getNextMove(List<MoveCommand> movesList) {
		return movesList.get(0);
	}
	
	//https://www.baeldung.com/java-dijkstra
	public void calcMinDistance(Coordinates startingPos, Entry<String, Coordinates> mapEntry) {
		
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
			previousNode.put(mapEntry.getValue(), startingPos);
			myMap.getFields().get(mapEntry.getValue()).setShortestPath(path);
			
		}
	}
	
	//https://www.baeldung.com/java-dijkstra
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
	
}

