package client.models.mapData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import client.controllers.NetworkController;
import client.models.mapData.enums.FortState;
import client.models.mapData.enums.MapFieldType;

public class MapValidator {
	// CODE TAKEN FROM https://www.geeksforgeeks.org/flood-fill-algorithm-implement-fill-paint/
	// I have never used Floodfill algorithm before so I learnt the idea from this website. 

	private List<Coordinates> alreadyVisited = new ArrayList<Coordinates>();
	private static final Logger logger = LoggerFactory.getLogger(MapValidator.class);
	
	//reachable nodes (in theory)
	private boolean checkCoordinates(ClientMap myMap) {

		for (int y = 0; y < myMap.getySize(); ++y) {	
			for(int x =0; x < myMap.getxSize(); ++x) {
				Coordinates pos = new Coordinates(x, y);
				if (myMap.getFields().containsKey(pos) == false) {
					return false;
				}
				if (myMap.getFields().get(pos).getPosition().getX() < 0 || myMap.getFields().get(pos).getPosition().getY() < 0) {
					return false;
				}
			}
		}

		return true;
		
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
	private Map<Coordinates, MapField> getGrassMountainFields(ClientMap myMap) {
		
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
	private void checkIfReachable(Coordinates startingPos, ClientMap mapToVerify, List<Coordinates> visitedNodes) {
		
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
	
	private boolean verifyNoOfFields(ClientMap mapToVerify) {
		
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
		if (checkCoordinates(myMap) == false) {
			logger.info("check coordinates is false");
			return false;
		}

		if (hasFort(myMap) == false) {
			logger.info("hasFort is false");
			return false;
		}
		
		if (hasNoIsland(myMap) == false) {
			logger.info("hasnoIsland is false");
			return false;
		}
		
		if (verifyNoOfFields(myMap) == false) {
			logger.info("verifynooffields is false");
			return false;
		}
		
		if (verifyLongSides(myMap) == false) {
			logger.info("verifylongsides is false");
			return false;
		}
		
		if (verifyShortSides(myMap) == false) {
			logger.info("verifyshortsides is false");
			return false;
		}
		
		if (verifyFieldTypesNo(myMap) == false) {
			logger.info("verifyfieldtypesno is false");
			return false;
		}

		return true;
		
	}
	
}
