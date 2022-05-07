package client.movement;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.models.gameData.GameStateData;
import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.models.mapData.enums.MapFieldType;
import client.models.mapData.enums.PlayerPositionState;
import client.models.mapData.enums.TreasureState;

public class TargetSelector {
	
	private Map<Coordinates, MapField> myHalf = new HashMap<Coordinates, MapField>();
	private Map<Coordinates, MapField> enemyHalf = new HashMap<Coordinates, MapField>();
	private ClientMap myMap;
	private Map<Coordinates, MapField> unvisitedTotal = new HashMap<Coordinates, MapField>();
	private GameStateData gameState;
	private static final Logger logger = LoggerFactory.getLogger(TargetSelector.class);
	
	public TargetSelector(GameStateData gameState) {
		super();
		if (gameState != null) {
			this.gameState = gameState;	
		}
		
	}
	
	public ClientMap getMyMap() {
		return myMap;
	}



	public void setMyMap(ClientMap myMap) {
		this.myMap = myMap;
	}



	
	public GameStateData getGameState() {
		return gameState;
	}



	public void setGameState(GameStateData gameState) {
		this.gameState = gameState;
	}



	public void setHalves() {
		Coordinates myFortPos = myMap.getMyFortField();	
		
		for( Map.Entry<Coordinates, MapField> mapEntry : myMap.getFields().entrySet() ) {
			if (mapEntry.getValue().getType() != MapFieldType.WATER) {
			
				unvisitedTotal.put(mapEntry.getKey(), mapEntry.getValue());
			}
		}
		logger.info("setHalves");
		// 8x8
		if (myMap.getxSize() == 8 && myMap.getySize() == 8) {
			if (myFortPos.getY() < 4) {
				for (int y=0; y<4; y++) {
					for(int x=0; x<8; x++) {
					
						Coordinates coords = new Coordinates(x, y);
						if (myMap.getFields().get(coords).getType() != MapFieldType.WATER) {
							myHalf.put(coords, myMap.getFields().get(coords));	
						}
						
						coords = new Coordinates(x, y+4);
						if (myMap.getFields().get(coords).getType() != MapFieldType.WATER) {
							enemyHalf.put(coords, myMap.getFields().get(coords));
						}
					}
				}
			} else {
				for (int y=4; y<8; y++) {
					for(int x=0; x<8; x++) {
					
						Coordinates coords = new Coordinates(x, y);
						if (myMap.getFields().get(coords).getType() != MapFieldType.WATER) {
							myHalf.put(coords, myMap.getFields().get(coords));	
						}
						
						coords = new Coordinates(x, y-4);
						if (myMap.getFields().get(coords).getType() != MapFieldType.WATER) {
							enemyHalf.put(coords, myMap.getFields().get(coords));
						}
					}
				}
			}
			
		} else {
			if (myFortPos.getX() < 8) {
				for (int y=0; y<4; y++) {
					for(int x=0; x<8; x++) {
					
						Coordinates coords = new Coordinates(x, y);
						if (myMap.getFields().get(coords).getType() != MapFieldType.WATER) {
							myHalf.put(coords, myMap.getFields().get(coords));
						}
						coords = new Coordinates(x+8, y);
						if (myMap.getFields().get(coords).getType() != MapFieldType.WATER) {
							enemyHalf.put(coords, myMap.getFields().get(coords));
						}
					}
				}
			} else {
				for (int y=0; y<4; y++) {
					for(int x=8; x<16; x++) {
					
						Coordinates coords = new Coordinates(x, y);
						if (myMap.getFields().get(coords).getType() != MapFieldType.WATER) {
							myHalf.put(coords, myMap.getFields().get(coords));
						}
						coords = new Coordinates(x-8, y);
						if (myMap.getFields().get(coords).getType() != MapFieldType.WATER) {
							enemyHalf.put(coords, myMap.getFields().get(coords));
						}
					}
				}
			}
		}
		
	}
	
	public Coordinates nextTarget() {
		
		Coordinates toRet = new Coordinates();
		//------------------------- test print
		System.out.println("acum suntem aici in target selector: " + this.gameState.getMyCurrentPosition(this.gameState.getFullMap()).getPosition().getX() + " " + this.gameState.getMyCurrentPosition(this.gameState.getFullMap()).getPosition().getY());
		//------------------------- test print
		
		boolean foundTreasure = (gameState.getTreasureIsPresentAt() != null);
		boolean foundEnemyFort = (gameState.getEnemyFortIsPresentAt() != null);
		boolean searchingForTreasure = (gameState.getTreasureIsPresentAt() == null);
		boolean searchingForEnemyFort = (gameState.getEnemyFortIsPresentAt() == null);
		
		//------------------------- test print
		System.out.println("foundTreasure" + foundTreasure);
		System.out.println("foundEnemyFort" + foundEnemyFort);
		System.out.println("searchingForTreasure" + searchingForTreasure);
		System.out.println("searchingForEnemyFort" + searchingForEnemyFort);
		//------------------------- test print
		
		if (myMap.getFields().get(gameState.getPlayerPosition()).getType() == MapFieldType.MOUNTAIN) {
			
			logger.info("i'm on a mountain");
			
			Map<String, Coordinates> fieldsAround = gameState.getPlayerPosition().getFieldsAround(myMap);
			
			for( Map.Entry<String, Coordinates> mapEntry : fieldsAround.entrySet() ) {
				
				// if it's not visited yet
				if (unvisitedTotal.containsKey(mapEntry.getValue())) {
					unvisitedTotal.remove(mapEntry.getValue());
				}
			
			}
		}
		
		// searching for treasure
		if (!foundTreasure && !searchingForEnemyFort && searchingForTreasure) {
			logger.info("I am searching for the treasure");
			//TODO
			toRet = nextAvailableNeighbour(gameState.getPlayerPosition(), myHalf);
			System.out.println("toRet: " + toRet.getX() + " " + toRet.getY());
			
		}
		
		// treasure has been picked up => searching for enemyFort
		if (foundTreasure && searchingForEnemyFort) {
			logger.info("I have the treasure and I am searching for the enemy Fort");
			//TODO
			toRet = nextAvailableNeighbour(gameState.getPlayerPosition(), enemyHalf);
			System.out.println("toRet: " + toRet.getX() + " " + toRet.getY());
		}
		
		// treasure is Present => go to treasure (foundTreasure)
		if (foundTreasure) {
			logger.info("The treasure is present and I am going towards it");
			toRet = gameState.getTreasureIsPresentAt();	
			System.out.println("toRet: " + toRet.getX() + " " + toRet.getY());
		}
		
		
		// enemyFort is Present => go to enemyFort (foundEnemyFort)
		if (foundEnemyFort) {
			logger.info("The fort is present and I am going towards it");
			toRet = gameState.getEnemyFortIsPresentAt();
			System.out.println("toRet: " + toRet.getX() + " " + toRet.getY());
		}
		System.out.println("toRet ALES: " + toRet.getX() + " " + toRet.getY());
		return toRet;
	}
	
	public Coordinates nextAvailableNeighbour(Coordinates pos, Map<Coordinates, MapField> mapHalf) {
		Coordinates toRet = new Coordinates();
		while (toRet == null) {
			
			Map<String, Coordinates> fieldsAround = pos.getFieldsAround(myMap);
			
			for( Map.Entry<String, Coordinates> mapEntry : fieldsAround.entrySet() ) {
				
				// if it's not visited yet
				if (unvisitedTotal.containsKey(mapEntry.getValue())) {
					toRet = mapEntry.getValue();
				}
				
				pos = mapEntry.getValue();
			}
		
		}
		
		// remove from unvisited;
		mapHalf.remove(toRet);
		unvisitedTotal.remove(toRet);
		return toRet;
		
	}
	
	
	
	
	

	
}
