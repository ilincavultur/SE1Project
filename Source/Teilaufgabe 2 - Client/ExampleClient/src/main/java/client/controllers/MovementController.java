package client.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.movement.PathCalculator;
import client.movement.enums.MoveCommand;

public class MovementController {
	
	private PathCalculator pathCalc;
	private MapField currentField;
	private ClientMap fullMap;
	
	private static final Logger logger = LoggerFactory.getLogger(MovementController.class);

	
	
	public MovementController() {
		super();
	}



	public MovementController(PathCalculator pathCalc, MapField currentField, ClientMap fullMap) {
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



	public MapField getCurrentField() {
		return currentField;
	}



	public void setCurrentField(MapField currentField) {
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
	
	
	
	public MovementController(PathCalculator pathCalc) {
		super();
		this.pathCalc = pathCalc;
	}

	public MoveCommand getNextMove() {
		Coordinates targetPosition = new Coordinates(0,0);
		MapField targetField = fullMap.getFields().get(targetPosition);
		List<Coordinates> path = pathCalc.getShortestPath(currentField, targetField);
		logger.info("path calculated :");
		System.out.println(path);
		
		//TODO get according move
		//return path.get(path.size()-1);
		return MoveCommand.DOWN;
	}
	
	//public void send N
	

}
