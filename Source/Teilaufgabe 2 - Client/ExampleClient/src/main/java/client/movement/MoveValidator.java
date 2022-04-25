package client.movement;

import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.models.mapData.enums.MapFieldType;
import client.movement.enums.MoveCommand;

public class MoveValidator {
	
	ClientMap fullMap;
	
	

	public MoveValidator(ClientMap fullMap) {
		super();
		this.fullMap = fullMap;
	}



	public boolean validateMove(Coordinates currPos, MoveCommand move) {
		if(move == MoveCommand.UP) {
			if (!fullMap.getFields().containsKey(currPos.getUpNeighbour())) {
				return false;
			} 
			if(fullMap.getFields().get(currPos.getUpNeighbour()).getType() == MapFieldType.WATER) {
				return false;
			}
		}
		if(move == MoveCommand.DOWN) {
			if (!fullMap.getFields().containsKey(currPos.getDownNeighbour())) {
				return false;
			} 
			if(fullMap.getFields().get(currPos.getDownNeighbour()).getType() == MapFieldType.WATER) {
				return false;
			}
		}
		if(move == MoveCommand.LEFT) {
			if (!fullMap.getFields().containsKey(currPos.getLeftNeighbour())) {
				return false;
			} 
			if(fullMap.getFields().get(currPos.getLeftNeighbour()).getType() == MapFieldType.WATER) {
				return false;
			}
		}
		if(move == MoveCommand.RIGHT) {
			if (!fullMap.getFields().containsKey(currPos.getRightNeighbour())) {
				return false;
			} 
			if(fullMap.getFields().get(currPos.getRightNeighbour()).getType() == MapFieldType.WATER) {
				return false;
			}
		}
		return true;
	}
}
