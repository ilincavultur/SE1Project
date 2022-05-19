package client.models.mapData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import client.models.mapData.enums.FortState;
import client.models.mapData.enums.MapFieldType;

public class MapValidator {
	// CODE TAKEN FROM https://www.geeksforgeeks.org/flood-fill-algorithm-implement-fill-paint/
	// I have never used Floodfill algorithm before so I learnt the idea from this website. 

	private List<Coordinates> alreadyVisited = new ArrayList<Coordinates>();
	
	//reachable nodes (in theory)
	public boolean checkCoordinates(ClientMap myMap) {
	
		int y0 = 0;
		int y1 = 0;
		int y2 = 0;
		int y3 = 0;
		for( Map.Entry<Coordinates, MapField> mapEntry : myMap.getFields().entrySet() ) {
			if (mapEntry.getKey().getX() < 0 || mapEntry.getKey().getY() < 0) {
				return false;
			}
				
			if (mapEntry.getKey().getY() == 0 ) {
				y0+=1;
			}
			if (mapEntry.getKey().getY() == 1 ) {
				y1+=1;		
			}
			if (mapEntry.getKey().getY() == 2 ) {
				y2+=1;
			}
			if (mapEntry.getKey().getY() == 3 ) {
				y3+=1;
			}
		}

		if (y0==8 && y1==8 && y2==8 && y3==8) {
			System.out.println("all good");
			return true;
		}
		System.out.println("all bad");
		return false;
		
	}

	public boolean hasFort(ClientMap mapToVerify) {
		for( Map.Entry<Coordinates, MapField> mapEntry : mapToVerify.getFields().entrySet() ) {
			if (mapEntry.getValue().getFortState() == FortState.MYFORT && mapEntry.getValue().getType() == MapFieldType.GRASS) {
				return true;
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
		
		return (alreadyVisited.size() == this.getGrassMountainFields(mapToVerify).size());
	}
	
	// CODE TAKEN FROM START https://www.geeksforgeeks.org/flood-fill-algorithm-implement-fill-paint/
	public void checkIfReachable(Coordinates startingPos, ClientMap mapToVerify, List<Coordinates> visitedNodes) {
		
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
	// CODE TAKEN FROM END https://www.geeksforgeeks.org/flood-fill-algorithm-implement-fill-paint/
	
	public boolean verifyNoOfFields(ClientMap mapToVerify) {
		
		int xSize = mapToVerify.getxSize();
		int ySize = mapToVerify.getySize();
		int xTimesy = xSize * ySize;

		return (xTimesy == 32 && xSize == 8 && ySize == 4);
		
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
		return (waterNo1 <= 3 && waterNo2 <=3) ;
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
		
		return (waterNo1 <= 1 && waterNo2 <=1) ;
	}
	
	public boolean verifyFieldTypesNo(ClientMap mapToVerify) {
		
		int waterFields = 0;
		int grassFields = 0;
		int mountainFields = 0;
		
		for( Map.Entry<Coordinates, MapField> mapEntry : mapToVerify.getFields().entrySet() ) {
			if (mapEntry.getValue().getType() == MapFieldType.GRASS) {
				grassFields+=1;
			}
			if (mapEntry.getValue().getType() == MapFieldType.MOUNTAIN) {
				mountainFields+=1;
			}
			if (mapEntry.getValue().getType() == MapFieldType.WATER) {
				waterFields+=1;
			}
		}


		return (waterFields >=4 && grassFields >= 15 && mountainFields >= 3);
	}
	
	public boolean validateMap(ClientMap myMap) {

		if (checkCoordinates(myMap) && hasFort(myMap) && hasNoIsland(myMap) && verifyNoOfFields(myMap) && verifyLongSides(myMap) && verifyShortSides(myMap) && verifyFieldTypesNo(myMap) ) {
			return true;
		}
		
		return false;
		
	}
	
}
