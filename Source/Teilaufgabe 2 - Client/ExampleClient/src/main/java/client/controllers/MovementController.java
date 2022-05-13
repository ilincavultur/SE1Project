package client.controllers;

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
		logger.info("setup");
	}
	
	public void updatePath() {
		logger.info("updatepath");
		this.targetSelector.setGameState(gameState);
		
		//test
		this.targetSelector.setGoPickUpTreasure(goPickUpTreasure);
		System.out.println("goPickupTreasure " + goPickUpTreasure);
		this.targetSelector.setGoBribeFort(goBribeFort);
		System.out.println("goBribeFort " + goBribeFort);
		//test
		
		// if i haven't gotten any path yet 
		if (this.movesList == null) {
			logger.info("updatepath: moves list == null");
			calcMovesToGoal();
			
		}
				
		// if treasure is present and I havent picked it up yet
		if (goPickUpTreasure == true && gameState.getTreasureIsPresentAt() != null && !gameState.getHasCollectedTreasure() && gameState.getHasCollectedTreasure() != null ) {
			logger.info("treasure is present and I havent picked it up yet");
			//goPickUpTreasure = true;
			calcMovesToGoal();
			
		}
		
		// if enemyFort is present and I have the treasure
		if (goBribeFort == true && gameState.getEnemyFortIsPresentAt() != null && gameState.getHasCollectedTreasure() != null) {
			logger.info("enemyFort is present and I have the treasure");
			//goBribeFort = true;
			calcMovesToGoal();
		}
				
		// if i reached the previous goal
		// aici fii atenta ca la pathcalculator nu cred ca e inclus targetul..
		if (this.movesList.isEmpty() || this.movesList.size() == 0) {
			logger.info("i reached prev goal");
			calcMovesToGoal();
		}
		
		
	}
	
	public void calcMovesToGoal() {
		
		this.targetSelector.setGameState(gameState);
		
		Coordinates targetPosition = this.targetSelector.nextTarget();
		
		// test
			while (currentField == targetPosition) {
				targetPosition = this.targetSelector.nextTarget();
			}
		// test
		
		//------------------------- test print
		System.out.println("target field" + targetPosition.getX() + targetPosition.getY());
		//------------------------- test print
		
		MapField targetField = fullMap.getFields().get(targetPosition);
		
		pathCalc.getShortestPath(currentField, targetField);
		
		this.setMovesList(pathCalc.getMovesPath(targetField));
		
		//------------------------- test print
		for (int i=0; i<this.movesList.size(); i++) {
			System.out.println("move " + movesList.get(i));
		}
				
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
			
		} // else updatepath? or update path before

		
		return null;
		
	}
}
