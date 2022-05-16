package client.models.mapData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import client.models.mapData.enums.FortState;
import client.models.mapData.enums.MapFieldType;

public class MapValidator {

	private List<Coordinates> alreadyVisited = new ArrayList<Coordinates>();

	public boolean hasFort(ClientMap mapToVerify) {
		//TODO my fort was once on a mountain
		for(int y =0; y < 4; y++) {
			for (int x = 0; x < 8; x++) {
				Coordinates pos = new Coordinates(x, y);
				
				if(mapToVerify.getFields().get(pos).getFortState()==FortState.MYFORT && mapToVerify.getFields().get(pos).getType() == MapFieldType.GRASS) {
					return true;
				}
				
				
			}
		}
		return false;
	}
	
	//reachable nodes (in theory)
	public Map<Coordinates, MapField> getGrassMountainFields(ClientMap myMap) {
		
		Map<Coordinates, MapField> fieldsGrassMountain = new HashMap<Coordinates, MapField>();
		
		for( Map.Entry<Coordinates, MapField> mapEntry : myMap.getFields().entrySet() ) {
			if (mapEntry.getValue().getType() == MapFieldType.GRASS || mapEntry.getValue().getType() == MapFieldType.MOUNTAIN) {
				fieldsGrassMountain.put(mapEntry.getKey(), mapEntry.getValue());
			}
		}
		
		return fieldsGrassMountain;
		
	}
	
	public boolean hasNoIsland(ClientMap mapToVerify) {
		
		this.alreadyVisited = new ArrayList<Coordinates>();
		
		Random randomNo = new Random();
		int randomX = randomNo.nextInt(8);
		int randomY = randomNo.nextInt(4);
		
		Coordinates startingPos = new Coordinates(randomX, randomY);
		
		while(mapToVerify.getFields().get(startingPos).getType() == MapFieldType.WATER) {
			randomX = randomNo.nextInt(8);
			randomY = randomNo.nextInt(4);
			startingPos = new Coordinates(randomX, randomY);
		}
		checkIfReachable(startingPos, mapToVerify, alreadyVisited);
		
		return alreadyVisited.size() == this.getGrassMountainFields(mapToVerify).size();
	}
	
	//floodfill
	public void checkIfReachable(Coordinates startingPos, ClientMap mapToVerify, List<Coordinates> visitedNodes) {
		
		// TODO inlocuieste || || || 
		if(!mapToVerify.getFields().containsKey(startingPos) || visitedNodes.contains(startingPos) || mapToVerify.getFields().get(startingPos).getType() == MapFieldType.WATER) {
			return;
		}else {
			visitedNodes.add(startingPos);
			checkIfReachable(new Coordinates(startingPos.getX() - 1, startingPos.getY()), mapToVerify, visitedNodes);
			checkIfReachable(new Coordinates(startingPos.getX() + 1, startingPos.getY()), mapToVerify, visitedNodes);
			checkIfReachable(new Coordinates(startingPos.getX(), startingPos.getY() - 1), mapToVerify, visitedNodes);
			checkIfReachable(new Coordinates(startingPos.getX(), startingPos.getY() + 1), mapToVerify, visitedNodes);
		}
	}
	
	public boolean verifyNoOfFields(ClientMap mapToVerify) {

		return (mapToVerify.getxSize() * mapToVerify.getySize() == 32 && mapToVerify.getxSize() == 8 && mapToVerify.getySize() == 4);
		
	}
	
	public boolean verifyLongSides(ClientMap mapToVerify) {
		int waterNo1 = 0;
		int waterNo2 = 0;
		//mapToVerify.xSize
		for (int x = 0; x < 8 ; x++) {
			Coordinates pos1 = new Coordinates(x, 0);
			Coordinates pos2 = new Coordinates(x, mapToVerify.getySize() - 1);
			if (mapToVerify.getFields().get(pos1).getType() == MapFieldType.WATER) {
				waterNo1++;
			}
			if (mapToVerify.getFields().get(pos2).getType() == MapFieldType.WATER) {
				waterNo2++;
			}
		}
		return waterNo1 <= 3 && waterNo2 <=3 ;
	}
	
	
	public boolean verifyShortSides(ClientMap mapToVerify) {
		
		int waterNo1 = 0;
		int waterNo2 = 0;
	
		for (int y = 0; y < 4 ; y++) {
			
			Coordinates pos1 = new Coordinates(0, y);
			Coordinates pos2 = new Coordinates(mapToVerify.getxSize() - 1, y);
			if (mapToVerify.getFields().get(pos1).getType() == MapFieldType.WATER) {
				waterNo1++;
			}
			if (mapToVerify.getFields().get(pos2).getType() == MapFieldType.WATER) {
				waterNo2++;
			}
			
		}
		
		return waterNo1 <= 1 && waterNo2 <=1 ;
	}
	
	public boolean verifyFieldTypesNo(ClientMap mapToVerify) {
		
		int waterFields = 4;
		int grassFields = 15;
		int mountainFields = 3;
		
		for (int y=0 ; y<4; y++) {
			for(int x=0; x<8; x++) {
				Coordinates pos = new Coordinates(x, y);
				if(mapToVerify.getFields().get(pos).getType()==MapFieldType.GRASS) {
					grassFields--;
				}
				if(mapToVerify.getFields().get(pos).getType()==MapFieldType.MOUNTAIN) {
					mountainFields--;
				}
				if(mapToVerify.getFields().get(pos).getType()==MapFieldType.WATER) {
					waterFields--;
				}
			}
		}

		return (waterFields <= 0 && grassFields <= 0 && mountainFields <= 0);
	}
	
	public boolean validateMap(ClientMap myMap) {
		
		if ( hasFort(myMap) && hasNoIsland(myMap) && verifyNoOfFields(myMap) && verifyLongSides(myMap) && verifyShortSides(myMap) && verifyFieldTypesNo(myMap) ) {
			return true;
		}
		
		return false;
		
	}
	
}
