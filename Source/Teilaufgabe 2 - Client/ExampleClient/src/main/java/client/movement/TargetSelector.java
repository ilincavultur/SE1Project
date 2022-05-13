package client.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.models.gameData.GameStateData;
import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.models.mapData.enums.FortState;
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
	// test 
	boolean goPickUpTreasure = false;
	boolean goBribeFort = false;
	// test
	
	
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
	
	public boolean isGoPickUpTreasure() {
		return goPickUpTreasure;
	}

	public void setGoPickUpTreasure(boolean goPickUpTreasure) {
		this.goPickUpTreasure = goPickUpTreasure;
	}

	public boolean isGoBribeFort() {
		return goBribeFort;
	}

	public void setGoBribeFort(boolean goBribeFort) {
		this.goBribeFort = goBribeFort;
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
		
		//------------------------- test print
		/*System.out.println("unvisitedTotal");
		for( Map.Entry<Coordinates, MapField> mapEntry : unvisitedTotal.entrySet() ) {
			System.out.println(mapEntry.getKey().getX() + " " + mapEntry.getKey().getY());
		}
		System.out.println("myHalf");
		for( Map.Entry<Coordinates, MapField> mapEntry : myHalf.entrySet() ) {
			System.out.println(mapEntry.getKey().getX() + " " + mapEntry.getKey().getY());
		}
		System.out.println("enemyHalf");
		for( Map.Entry<Coordinates, MapField> mapEntry : enemyHalf.entrySet() ) {
			System.out.println(mapEntry.getKey().getX() + " " + mapEntry.getKey().getY());
		}*/
		//------------------------- test print
		
	}
	
	public Coordinates nextTarget() {
		
		Coordinates toRet = new Coordinates();
		toRet.setX(-1);
		toRet.setY(-1);
		
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
		
		logger.info("wtf");
		if (myMap.getFields().get(gameState.getPlayerPosition()).getType() == MapFieldType.MOUNTAIN) {
			
			logger.info("i'm on a mountain");
			
			Map<String, Coordinates> fieldsAround = gameState.getPlayerPosition().getFieldsAroundMountain(myMap);
			
			for( Map.Entry<String, Coordinates> mapEntry : fieldsAround.entrySet() ) {
				
				if (unvisitedTotal.containsKey(mapEntry.getValue())) {
					unvisitedTotal.remove(mapEntry.getValue());
				}
				
				// if it's my treasure then it means it hasn't been picked up yet
				if (myMap.getFields().get(mapEntry.getValue()).getTreasureState() == TreasureState.MYTREASURE) {
					// if it's not visited yet
				
					return mapEntry.getValue();
				}
				
				if (foundTreasure && myMap.getFields().get(mapEntry.getValue()).getFortState() == FortState.ENEMYFORT) {
					// if it's not visited yet
					
					return mapEntry.getValue();
				}
			
			}
		}
		
		// find a way to return 
		// searching for treasure
		while (toRet.getX() < 0 || toRet.getY() < 0) {
			if (!foundTreasure && searchingForTreasure) {
				
				logger.info("I am searching for the treasure");
		
				toRet = nextAvailableNeighbour(gameState.getPlayerPosition(), myHalf);
				//System.out.println("toRet: " + toRet.getX() + " " + toRet.getY());
				return toRet;
			}
			
			// treasure has been picked up => searching for enemyFort
			if (foundTreasure && searchingForEnemyFort) {
				
				logger.info("I have the treasure and I am searching for the enemy Fort");
		
				toRet = nextAvailableNeighbour(gameState.getPlayerPosition(), enemyHalf);
				//System.out.println("toRet: " + toRet.getX() + " " + toRet.getY());
				return toRet;
			
			}
			
			// treasure is Present => go to treasure (foundTreasure)
			if (foundTreasure && this.goPickUpTreasure == true) {
				
				logger.info("The treasure is present and I am going towards it");
				
				toRet = gameState.getTreasureIsPresentAt();	
				//System.out.println("toRet: " + toRet.getX() + " " + toRet.getY());
				return toRet;
				
			}
			
			
			// enemyFort is Present => go to enemyFort (foundEnemyFort)
			if (foundEnemyFort && this.goBribeFort == true) {
				
				logger.info("The fort is present and I am going towards it");
				
				toRet = gameState.getEnemyFortIsPresentAt();
				//System.out.println("toRet: " + toRet.getX() + " " + toRet.getY());
				return toRet;
				
				
			}
		}
		
		System.out.println("toRet ALES: " + toRet.getX() + " " + toRet.getY());
		return toRet;
	}
	
	public Coordinates nextAvailableNeighbour(Coordinates pos, Map<Coordinates, MapField> mapHalf) {
		
		Coordinates toRet = new Coordinates();
		
		toRet.setX(-1);
		toRet.setY(-1);
	
		while (toRet.getX() < 0 || toRet.getY() < 0) {
			
			Map<String, Coordinates> fieldsAround = pos.getFieldsAround(myMap);	
			
			for( Map.Entry<String, Coordinates> mapEntry : fieldsAround.entrySet() ) {
				
				// if it's not visited yet
				if (mapHalf.containsKey(mapEntry.getValue()) && unvisitedTotal.containsKey(mapEntry.getValue())) {
				
					toRet = mapEntry.getValue();
					//------------------------- test print
					System.out.println("next unvisited neighbour: " + toRet.getX() + " " + toRet.getY());
					//------------------------- test print
					break;
				}
				
				pos = mapEntry.getValue();
			}
		
		}
		
		// daca totusi toate si already visited
		/*if (toRet.getX() < 0 || toRet.getY() < 0) {
			
		}*/
		
		// remove from unvisited;
		//mapHalf.remove(toRet);
		unvisitedTotal.remove(toRet);
		updateMapHalf();
		return toRet;
		
	}
	
	public void updateMapHalf() {
		
		Set<Entry<Coordinates, MapField>> entrySet = myHalf.entrySet();
		
		Iterator<Entry<Coordinates, MapField>> itr = entrySet.iterator();
		
		while (itr.hasNext()) {
			Entry<Coordinates, MapField> entry = itr.next();
			if (!unvisitedTotal.containsKey(entry.getKey())) {
				itr.remove();
				System.out.println("i removed: " + entry.getKey().getX() + " " + entry.getKey().getY());
			}
		}
		
		entrySet = enemyHalf.entrySet();
		
		itr = entrySet.iterator();
		
		while (itr.hasNext()) {
			Entry<Coordinates, MapField> entry = itr.next();
			if (!unvisitedTotal.containsKey(entry.getKey())) {
				itr.remove();
				System.out.println("i removed: " + entry.getKey().getX() + " " + entry.getKey().getY());
			}
		}
		
		//------------------------- test print
		/*System.out.println("myhalf remained:");
		for( Entry<Coordinates, MapField> mapEntry : myHalf.entrySet() ) {

				
				System.out.println(mapEntry.getKey().getX() + " " + mapEntry.getKey().getY());
	
		}
		System.out.println("enemyhalf remained:");
		for( Entry<Coordinates, MapField> mapEntry : enemyHalf.entrySet() ) {

				
				System.out.println(mapEntry.getKey().getX() + " " + mapEntry.getKey().getY());
	
		}*/
		//------------------------- test print
		
	}
	
	
	
	

	
}
