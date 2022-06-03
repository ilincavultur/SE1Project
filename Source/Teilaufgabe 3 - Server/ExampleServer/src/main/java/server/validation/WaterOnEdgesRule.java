package server.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import server.enums.MapFieldType;
import server.enums.MoveCommand;
import server.exceptions.HalfMapException;
import server.models.Coordinates;
import server.models.GameState;
import server.models.InternalHalfMap;
import server.models.Player;
import server.models.MapNode;

public class WaterOnEdgesRule implements IRuleValidation{

	
	public void verifyLongSides(HalfMap halfMap) {
		
		int waterNo1 = 0;
		int waterNo2 = 0;
		
		Map<Coordinates, HalfMapNode> fields = new HashMap<Coordinates, HalfMapNode>();
		
		for (HalfMapNode node: halfMap.getMapNodes()) {
			
			Coordinates pos = new Coordinates(node.getX(), node.getY());
			fields.put(pos, node);
			
		}
		
		for (int x = 0; x < 8 ; x++) {
			Coordinates pos1 = new Coordinates(x, 0);
			Coordinates pos2 = new Coordinates(x, 7);
			if (fields.get(pos1).getTerrain() == ETerrain.Water) {
				waterNo1++;
			}
			if (fields.get(pos2).getTerrain() == ETerrain.Water) {
				waterNo2++;
			}
		}
		
		if (waterNo1 > 3) {
			throw new HalfMapException("Water number exceeded on Long Side", "Maximum 3 Waters allowed, but found: " + waterNo1);
		}
		
		if (waterNo2 > 3) {
			throw new HalfMapException("Water number exceeded on Long Side", "Maximum 3 Waters allowed, but found: " + waterNo2);
		}
		
	}
	
	public void verifyShortSides(HalfMap halfMap) {
		
		int waterNo1 = 0;
		int waterNo2 = 0;
		
		Map<Coordinates, HalfMapNode> fields = new HashMap<Coordinates, HalfMapNode>();
		
		for (HalfMapNode node: halfMap.getMapNodes()) {
			
			Coordinates pos = new Coordinates(node.getX(), node.getY());
			fields.put(pos, node);
			
		}
		
		for (int y = 0; y < 4 ; y++) {
			Coordinates pos1 = new Coordinates(0, y);
			Coordinates pos2 = new Coordinates(7, y);
			if (fields.get(pos1).getTerrain() == ETerrain.Water) {
				waterNo1++;
			}
			if (fields.get(pos2).getTerrain() == ETerrain.Water) {
				waterNo2++;
			}
		}
		
		if (waterNo1 > 1) {
			throw new HalfMapException("Water number exceeded on Short Side", "Maximum 1 Waters allowed, but found: " + waterNo1);
		}
		
		if (waterNo2 > 1) {
			throw new HalfMapException("Water number exceeded on Short Side", "Maximum 1 Waters allowed, but found: " + waterNo2);
		}
		
	}

	@Override
	public void validatePlayerReg(Map<String, GameState> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateHalfMap(HalfMap halfMap) {
		verifyLongSides(halfMap);
		verifyShortSides(halfMap);
		
	}

	@Override
	public void validateGameState(Map<String, GameState> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateMove(Map<String, GameState> games, PlayerMove move, UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void myTurn(Map<String, GameState> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}


}
