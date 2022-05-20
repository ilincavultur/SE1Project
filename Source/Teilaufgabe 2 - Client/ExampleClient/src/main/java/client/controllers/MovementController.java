package client.controllers;

import java.util.ArrayList;
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
	private List<MoveCommand> movesList;
	private GameStateData gameState;
	private boolean goPickUpTreasure = false;
	private boolean goBribeFort = false;
	private List<Coordinates> unvisitedTotal = new ArrayList<Coordinates>();

	private static final Logger logger = LoggerFactory.getLogger(MovementController.class);

	public MovementController() {
		super();
		this.pathCalc = new PathCalculator(fullMap);
		this.targetSelector = new TargetSelector(gameState);
	}
	
	public MovementController(List<MoveCommand> ml, PathCalculator pc) {
		super();
		this.movesList = ml;
		this.pathCalc = pc;
	}

	public MovementController(PathCalculator pathCalc, Coordinates currentField, ClientMap fullMap) {
		super();
		this.pathCalc = pathCalc;
		this.currentField = currentField;
		this.fullMap = fullMap;
		
	}

	public PathCalculator getPathCalc() {
		return pathCalc;
	}

	public void setGameState(GameStateData gameState) {
		this.gameState = gameState;
		this.targetSelector.setGameState(gameState);
	}

	public void setCurrentField(Coordinates currentField) {
		this.currentField = currentField;
	}

	public void setFullMap(ClientMap fullMap) {
		this.fullMap = fullMap;
	}

	public void setFullMap() {
		pathCalc.setMyMap(fullMap);
		
	}
	
	public List<Coordinates> getUnvisitedTotal() {
		return unvisitedTotal;
	}

	public void setUnvisitedTotal(List<Coordinates> unvisitedTotal) {
		this.unvisitedTotal = unvisitedTotal;
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
		
		logger.info("setup");
	}
	
	public void updatePath() {

		logger.info("updatepath");
		
		this.targetSelector.setGameState(gameState);
	
		this.unvisitedTotal = this.targetSelector.getUnvisitedTotal();
		
		
		if (unvisitedTotal.contains(gameState.getPlayerPosition())) {
			unvisitedTotal.remove(gameState.getPlayerPosition());	
		}
			
		this.targetSelector.setUnvisitedTotal(unvisitedTotal);
		
		// if i haven't gotten any path yet 
		if (this.movesList == null) {
			
			logger.info("updatepath: moves list == null");
			calcMovesToGoal();
			
		}

		// if treasure is present and I havent picked it up yet
		if (gameState.getTreasureIsPresentAt() != null && gameState.getHasCollectedTreasure() != null && !gameState.getHasCollectedTreasure() && goPickUpTreasure == false) {
			goPickUpTreasure = true;
			logger.info("treasure is present and I havent picked it up yet");
			calcMovesToGoal();	
	
		}
		
		// if I have picked up the treasure but still haven't found the enemy fort
		if (gameState.getHasCollectedTreasure() != null && gameState.getHasCollectedTreasure() == true && (this.movesList.isEmpty() || this.movesList.size() == 0 || targetSelector.getNextTarget().equals(currentField)) ) {
			
			logger.info("I have picked up the treasure but still haven't found the enemy fort");
			calcMovesToGoal();
			
		}
		
		// if enemyFort is present and I have the treasure
		if (gameState.getEnemyFortIsPresentAt() != null && gameState.getHasCollectedTreasure() != null && goBribeFort == false) {
			goBribeFort = true;
			logger.info("enemyFort is present and I have the treasure");
			calcMovesToGoal();
		
		}
				
		// if i reached the previous goal
		if (targetSelector.getNextTarget().equals(currentField) || this.movesList.isEmpty() || this.movesList.size() == 0) {
			
			logger.info("i reached prev goal");
			calcMovesToGoal();
			
		}
		System.out.println("current next target is " + targetSelector.getNextTarget().getX() + " " + targetSelector.getNextTarget().getY() );
		
	}
	
	private void calcMovesToGoal() {
		
		this.targetSelector.setGameState(gameState);
		
		Coordinates targetPosition = this.targetSelector.nextTarget();
		
		if (currentField.equals(targetPosition)) {
			targetPosition = this.targetSelector.nextTarget();
		}
		
		MapField targetField = fullMap.getFields().get(targetPosition);
		
		pathCalc.getShortestPath(currentField, targetField);
		
		//targetField.setShortestPath(pathCalc.getShortestPath());
	
		this.setMovesList(pathCalc.getMovesPath(targetField));

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
