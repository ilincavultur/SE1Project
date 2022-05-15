package client.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.models.gameData.GameStateData;
import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.movement.PathCalculator;
import client.movement.TargetSelector;
import client.movement.enums.MoveCommand;

public class MovementController {
	
	private PathCalculator pathCalc;
	private TargetSelector targetSelector;
	private Coordinates currentField;
	private ClientMap fullMap;
	List<MoveCommand> movesList;
	GameStateData gameState;
	boolean goPickUpTreasure = false;
	boolean goBribeFort = false;
	List<Coordinates> shortestPath = new ArrayList<Coordinates>();
	
	
	private static final Logger logger = LoggerFactory.getLogger(MovementController.class);

	public MovementController() {
		super();
		this.pathCalc = new PathCalculator(fullMap);
		this.targetSelector = new TargetSelector(gameState);
	}

	public MovementController(PathCalculator pathCalc, Coordinates currentField, ClientMap fullMap) {
		super();
		this.pathCalc = pathCalc;
		this.currentField = currentField;
		this.fullMap = fullMap;
		
	}

	
	public List<Coordinates> getShortestPath() {
		return shortestPath;
	}

	public void setShortestPath(List<Coordinates> shortestPath) {
		this.shortestPath = shortestPath;
	}

	public PathCalculator getPathCalc() {
		return pathCalc;
	}

	public void setPathCalc(PathCalculator pathCalc) {
		this.pathCalc = pathCalc;
	}

	public GameStateData getGameState() {
		return gameState;
	}



	public void setGameState(GameStateData gameState) {
		this.gameState = gameState;
		this.targetSelector.setGameState(gameState);
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

	public TargetSelector getTargetSelector() {
		return targetSelector;
	}

	public void setTargetSelector(TargetSelector targetSelector) {
		this.targetSelector = targetSelector;
	}

	public Coordinates getCurrentField() {
		return currentField;
	}

	public void setCurrentField(Coordinates currentField) {
		this.currentField = currentField;
	}

	public ClientMap getFullMap() {
		return fullMap;
	}

	public void setFullMap(ClientMap fullMap) {
		this.fullMap = fullMap;
	}

	public void setFullMap() {
		pathCalc.setMyMap(fullMap);
		
	}
	
	public List<MoveCommand> getMovesList() {
		return movesList;
	}

	public void setMovesList(List<MoveCommand> movesList) {
		this.movesList = movesList;
	}

	public MovementController(PathCalculator pathCalc) {
		super();
		this.pathCalc = pathCalc;
	}
	
	public void setUp() {
		this.targetSelector.setMyMap(fullMap);
		this.targetSelector.setHalves();
		this.targetSelector.setGameState(gameState);
		this.pathCalc.setUnvisitedTotal(this.targetSelector.getUnvisitedTotal());
		
		logger.info("setup");
	}
	
	public void updatePath() {

		logger.info("updatepath");
		this.targetSelector.setGameState(gameState);
		//this.setShortestPath(pathCalc.getShortestPath());
		this.pathCalc.setUnvisitedTotal(this.targetSelector.getUnvisitedTotal());
		//test
		/*this.targetSelector.setGoPickUpTreasure(goPickUpTreasure);
		System.out.println("goPickupTreasure " + goPickUpTreasure);
		this.targetSelector.setGoBribeFort(goBribeFort);
		System.out.println("goBribeFort " + goBribeFort);*/
		
		// if i haven't gotten any path yet 
		if (this.movesList == null) {
			
			logger.info("updatepath: moves list == null");
			calcMovesToGoal();
			
		}
		
		// if current path that i am following is only made of already visited nodes
		//pathCalc.getUnvisitedTotal()
		if (Collections.disjoint(this.targetSelector.getUnvisitedTotal(), pathCalc.getShortestPath())) {
			logger.info("my path is only made of already visited nodes");
			calcMovesToGoal();
		}
		
		// if treasure is present and I havent picked it up yet
		//goPickUpTreasure == true && 
		if (gameState.getTreasureIsPresentAt() != null && gameState.getHasCollectedTreasure() != null && !gameState.getHasCollectedTreasure() && goPickUpTreasure == false) {
			goPickUpTreasure = true;
			logger.info("treasure is present and I havent picked it up yet");
			calcMovesToGoal();	
	
		}
		
		// if I have picked up the treasure but still haven't found the enemy fort
		//goBribeFort == false && 
		// || this.movesList.size() == 1
		if (gameState.getHasCollectedTreasure() != null && gameState.getHasCollectedTreasure() == true && (this.movesList.isEmpty() || this.movesList.size() == 0)) {
			
			logger.info("I have picked up the treasure but still haven't found the enemy fort");
			calcMovesToGoal();
			
		}
		
		// if enemyFort is present and I have the treasure
		//goBribeFort == true && 
		if (gameState.getEnemyFortIsPresentAt() != null && gameState.getHasCollectedTreasure() != null && goBribeFort == false) {
			goBribeFort = true;
			logger.info("enemyFort is present and I have the treasure");
			calcMovesToGoal();
		
		}
				
		// if i reached the previous goal
		// aici fii atenta ca la pathcalculator nu cred ca e inclus targetul..
		//|| this.movesList.size() == 1
		if (this.movesList.isEmpty() || this.movesList.size() == 0) {
			
			logger.info("i reached prev goal");
			calcMovesToGoal();
			
		}
		
		
	}
	
	public void calcMovesToGoal() {
		
		this.targetSelector.setGameState(gameState);
		
		Coordinates targetPosition = this.targetSelector.nextTarget();
		
		// test
		if (currentField.equals(targetPosition)) {
			targetPosition = this.targetSelector.nextTarget();
		}
		// test
		
		//------------------------- test print
		System.out.println("target field " + targetPosition.getX() + " " + targetPosition.getY());
		//------------------------- test print
		
		MapField targetField = fullMap.getFields().get(targetPosition);
		
		pathCalc.getShortestPath(currentField, targetField);
		
		//pathCalc.getUnvisitedTotal().containsAll(pathCalc.getShortestPath())
		/*if (Collections.disjoint(pathCalc.getUnvisitedTotal(), pathCalc.getShortestPath())) {
			logger.info("my path is only made of already visited nodes");
			pathCalc.getShortestPath(currentField, targetField);	
		}*/

		this.setMovesList(pathCalc.getMovesPath(targetField));
		 
		
		//------------------------- test print
		/*logger.info("moves path:");
		for (int i=0; i<this.movesList.size(); i++) {
			System.out.println("move " + movesList.get(i));
		}*/
				
		//------------------------- test print
		
	}

	public MoveCommand getNextMove() {

		MoveCommand toRet = MoveCommand.DOWN;
		
		if (this.movesList != null && this.movesList.size()!=0) {
			
			toRet = pathCalc.getNextMove(this.movesList);	
			
			//------------------------- test print
			System.out.println("move: " + toRet.toString());
			//------------------------- test print
			
			this.movesList.remove(0);
			
			return toRet;
			
		} 

		return null;
		
	}
}
