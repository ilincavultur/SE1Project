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
import client.models.mapData.enums.MapFieldType;

public class TargetSelector {
	
	private Map<Coordinates, MapField> myHalf = new HashMap<Coordinates, MapField>();
	private Map<Coordinates, MapField> myHalfCopy = new HashMap<Coordinates, MapField>();
	private Map<Coordinates, MapField> enemyHalf = new HashMap<Coordinates, MapField>();
	private Map<Coordinates, MapField> enemyHalfCopy = new HashMap<Coordinates, MapField>();
	private ClientMap myMap;
	private List<Coordinates> unvisitedTotal = new ArrayList<Coordinates>();
	private GameStateData gameState;
	private static final Logger logger = LoggerFactory.getLogger(TargetSelector.class);
	//private boolean goPickUpTreasure = false;
	//private boolean goBribeFort = false;
	private boolean searchingForEnemyFort = false;
	private boolean searchingForTreasure = true;
	private Coordinates nextTarget = new Coordinates();
	
	
	public TargetSelector(GameStateData gameState) {
		super();
		if (gameState != null) {
			this.gameState = gameState;	
		}
		
	}

	public void setMyMap(ClientMap myMap) {
		this.myMap = myMap;
	}

	public List<Coordinates> getUnvisitedTotal() {
		return unvisitedTotal;
	}
	
	public void setUnvisitedTotal(List<Coordinates> ut) {
		this.unvisitedTotal = ut;
	}

	public void setGameState(GameStateData gameState) {
		this.gameState = gameState;
	}
	
	public Coordinates getNextTarget() {
		return nextTarget;
	}

	public void setHalves() {
		Coordinates myFortPos = myMap.getMyFortField();	
		
		for( Map.Entry<Coordinates, MapField> mapEntry : myMap.getFields().entrySet() ) {
			if (mapEntry.getValue().getType() != MapFieldType.WATER) {
			
				//unvisitedTotal.put(mapEntry.getKey(), mapEntry.getValue());
				unvisitedTotal.add(mapEntry.getKey());
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
		myHalfCopy = myHalf;
		enemyHalfCopy = enemyHalf;
	
		
	}
	
	public Coordinates nextTarget() {
		
		unvisitedTotal.remove(gameState.getPlayerPosition());
		updateMapHalf();
		
		Coordinates toRet = new Coordinates();
		toRet.setX(-1);
		toRet.setY(-1);
		
		boolean pickedUpTreasure = (gameState.getHasCollectedTreasure() != null && gameState.getHasCollectedTreasure() == true);
		boolean foundTreasure = (gameState.getTreasureIsPresentAt() != null); // treasure is present i didn t get it yet!!
		boolean foundEnemyFort = (gameState.getEnemyFortIsPresentAt() != null); // enemy fort is present

		if (pickedUpTreasure && gameState.getEnemyFortIsPresentAt() == null) {
			searchingForTreasure = false;
			searchingForEnemyFort = true;
		}
		if (gameState.getEnemyFortIsPresentAt() != null) {
		
			searchingForEnemyFort = false;
		}
		if (foundTreasure) {
			searchingForTreasure = false;
		}
	
		if (myMap.getFields().get(gameState.getPlayerPosition()).getType() == MapFieldType.MOUNTAIN) {
			
			logger.info("i'm on a mountain");
			
			Map<String, Coordinates> fieldsAround = gameState.getPlayerPosition().getFieldsAroundMountain(myMap);
			
			for( Map.Entry<String, Coordinates> mapEntry : fieldsAround.entrySet() ) {
				
				if (unvisitedTotal.contains(mapEntry.getValue())) {
					unvisitedTotal.remove(mapEntry.getValue());
					updateMapHalf();
				}
		
			}
		}
		
		// find a way to return 
		// searching for treasure
		while (toRet.getX() < 0 || toRet.getY() < 0) {
			if (!pickedUpTreasure && !foundTreasure && searchingForTreasure) {
				
				logger.info("I am searching for the treasure");
		
				toRet = nextAvailableNeighbour(gameState.getPlayerPosition(), myHalf);
				this.nextTarget = toRet;
				
				return toRet;
			}
			
			// treasure is Present => go to treasure (foundTreasure)
			// && this.goPickUpTreasure == true
			if (!pickedUpTreasure && foundTreasure ) {
				
				logger.info("The treasure is present and I am going towards it");
				searchingForTreasure = false;
				toRet = gameState.getTreasureIsPresentAt();	
				this.nextTarget = toRet;
				
				return toRet;
				
			}
			
			// treasure has been picked up => searching for enemyFort
			if (pickedUpTreasure && searchingForEnemyFort) {
				searchingForTreasure = false;
				logger.info("I have the treasure and I am searching for the enemy Fort");
				searchingForEnemyFort = true;
				toRet = nextAvailableNeighbour(gameState.getPlayerPosition(), enemyHalf);
				this.nextTarget = toRet;
		
				return toRet;
			
			}
			
			// enemyFort is Present => go to enemyFort (foundEnemyFort)
			//&& this.goBribeFort == true
			if (pickedUpTreasure && foundEnemyFort ) {
				searchingForTreasure = false;
				logger.info("The fort is present and I am going towards it");
				searchingForEnemyFort = false;
				toRet = gameState.getEnemyFortIsPresentAt();
				this.nextTarget = toRet;
			
				return toRet;
			
			}
		}
		

		return toRet;
	}
	
	public Coordinates nextAvailableNeighbour(Coordinates pos, Map<Coordinates, MapField> mapHalf) {
		logger.info("calculating next available neighbour");
		Coordinates toRet = new Coordinates();
		
		toRet.setX(-1);
		toRet.setY(-1);

		while ((toRet.getX() < 0 || toRet.getY() < 0)) {
			
			Map<String, Coordinates> fieldsAround = pos.getFieldsAround(myMap);	

			for( Map.Entry<String, Coordinates> mapEntry : fieldsAround.entrySet() ) {
				logger.info("calculating next available neighbour");
				// if it's not visited yet
				
				if (mapHalf.containsKey(mapEntry.getValue()) && unvisitedTotal.contains(mapEntry.getValue())) {
					
					toRet = mapEntry.getValue();
					//------------------------- test print
					System.out.println("next unvisited neighbour: " + toRet.getX() + " " + toRet.getY());
					//------------------------- test print
					break;
				} 

			}
			
			break;
		}
		while (toRet.getX() < 0 || toRet.getY() < 0) {
			for( Entry<Coordinates, MapField> mapEntry : mapHalf.entrySet() ) {
				
				toRet = mapEntry.getKey();
				break;
			}
				
		}

		return toRet;
		
	}
	
	private void updateMapHalf() {
		
		Set<Entry<Coordinates, MapField>> entrySet = myHalf.entrySet();
		
		Iterator<Entry<Coordinates, MapField>> itr = entrySet.iterator();
		
		while (itr.hasNext()) {
			Entry<Coordinates, MapField> entry = itr.next();
			if (!unvisitedTotal.contains(entry.getKey())) {
				itr.remove();
		
			}
		}
		
		entrySet = enemyHalf.entrySet();
		
		itr = entrySet.iterator();
		
		while (itr.hasNext()) {
			Entry<Coordinates, MapField> entry = itr.next();
			if (!unvisitedTotal.contains(entry.getKey())) {
				itr.remove();
			
			}
		}

	}
	
	
}
