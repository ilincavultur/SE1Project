package client.controllers;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.models.gameData.GameStateData;
import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.movement.MoveValidator;
import client.movement.PathCalculator;
import client.movement.TargetSelector;
import client.movement.enums.MoveCommand;

public class MovementController {
	
	private PathCalculator pathCalc;
	private TargetSelector targetSelector;
	//private MapField currentField;
	private Coordinates currentField;
	private ClientMap fullMap;
	List<MoveCommand> movesList;
	GameStateData gameState;
	
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
		// if i haven't gotten any path yet 
		if (this.movesList == null) {
			calcMovesToGoal();
		}
				
		// if treasure is present and I havent picked it up yet
		if (gameState.getTreasureIsPresentAt() != null && !gameState.getHasCollectedTreasure()) {
			calcMovesToGoal();
		}
		
		// if enemyFort is present and I have the treasure
		if (gameState.getEnemyFortIsPresentAt() != null && gameState.getHasCollectedTreasure()) {
			calcMovesToGoal();
		}
		
		// if i reached the previous goal
		// aici fii atenta ca la pathcalculator nu cred ca e inclus targetul..
		if (this.movesList.isEmpty()) {
			calcMovesToGoal();
		}
		
		
	}
	
	public void calcMovesToGoal() {
		//Coordinates targetPosition = new Coordinates(fullMap.getxSize()-1,fullMap.getySize()-1);
		//MapField targetField = fullMap.getFields().get(targetPosition);
		
		this.targetSelector.setGameState(gameState);
		
		Coordinates targetPosition = this.targetSelector.nextTarget();
		MapField targetField = fullMap.getFields().get(targetPosition);
		
		System.out.println("current field" + currentField.getX() + currentField.getY());
		System.out.println("target field" + targetField.getPosition().getX() + targetField.getPosition().getY());
		
		pathCalc.getShortestPath(currentField, targetField);
		this.setMovesList(pathCalc.getMovesPath(targetField));
		
		/*for (int i=0; i<this.getMovesList().size(); i++) {
		
					System.out.println (this.getMovesList().get(i).toString() + " " + this.getMovesList().get(i).toString());
			
		}*/
	}

	public MoveCommand getNextMove() {
		//System.out.println("acum suntem aici : " + this.getCurrentField().getPosition().getX() + " " + this.getCurrentField().getPosition().getY());
		
		
	

		MoveCommand toRet = MoveCommand.DOWN;
		if (this.movesList != null && this.movesList.size()!=0) {
			
			
			toRet = pathCalc.getNextMove(this.movesList);	
			
			
			
			System.out.println("move: " + toRet.toString());
			this.movesList.remove(0);
			return toRet;
		} // else updatepath? or update path before

		
		return null;
		//calculate shortest path from current field to target field => this adds it to the target Field node
		
		
		
		
		
		//logger.info("path calculated :");
		
		
		
		/*

		//MoveCommand nextPos = calculateMove(currentField, path.get(i));
		//TODO get according move
		//return path.get(1);
		
		Random randNo = new Random();
		
		boolean okau = false;
		while (okau == false) {
			int moveCommand = randNo.nextInt(4);
			if (moveCommand == 0) {
				if (validateMove(MoveCommand.RIGHT)) {
					okau = true;
					//this.setCurrentField(fullMap.getFields().get(currentField.getPosition().getRightNeighbour()));
					
					return MoveCommand.RIGHT;
				}
			}
			
			if (moveCommand == 1) {
				if (validateMove(MoveCommand.LEFT)) {
					okau = true;
					//this.setCurrentField(fullMap.getFields().get(currentField.getPosition().getLeftNeighbour()));
					return MoveCommand.LEFT;
				}	
			}
			
			if (moveCommand == 2) {
				if (validateMove(MoveCommand.UP)) {
					okau = true;
					//this.setCurrentField(fullMap.getFields().get(currentField.getPosition().getUpNeighbour()));
					return MoveCommand.UP;
				}
			}
			
			if (moveCommand == 3) {
				if (validateMove(MoveCommand.DOWN)) {
					okau = true;
					//this.setCurrentField(fullMap.getFields().get(currentField.getPosition().getDownNeighbour()));
					return MoveCommand.DOWN;
				}
			}
		}
		
		
		//if (validateMove(path.get))
		*/
	}
	
	/*
	
	public MoveCommand calculateMove(Coordinates currPos, Coordinates nextPos) {
		if ( currPos.getUpNeighbour() == nextPos ) {
			return MoveCommand.UP;
		}
	
		if ( currPos.getDownNeighbour() == nextPos ) {
			return MoveCommand.DOWN;
		}
		
		if ( currPos.getLeftNeighbour() == nextPos ) {
			return MoveCommand.LEFT;
		}
		
		if ( currPos.getRightNeighbour() == nextPos ) {
			return MoveCommand.RIGHT;
		}
		return null;
	}
	*/
	/*public boolean validateMove(MoveCommand move) {
		MoveValidator moveValidator = new MoveValidator(this.fullMap);
		
		return moveValidator.validateMove(currentField.getPosition(), move);
	
	}*/
	
	
	

}
