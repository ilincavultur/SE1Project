package server.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import server.models.MapNode;
import server.models.Player;

public class NoIslandsRule implements IRuleValidation{
	
	private List<Coordinates> alreadyVisited = new ArrayList<Coordinates>();

	
	//reachable nodes (in theory)
	private Map<Coordinates, HalfMapNode> getGrassMountainFields(HalfMap myMap) {
		
		Map<Coordinates, HalfMapNode> fieldsGrassMountain = new HashMap<Coordinates, HalfMapNode>();
		
		for (HalfMapNode node: myMap.getMapNodes()) {
			if (node.getTerrain() == ETerrain.Grass || node.getTerrain() == ETerrain.Mountain) {
				Coordinates pos = new Coordinates(node.getX(), node.getY());
				fieldsGrassMountain.put(pos, node);
			}
		}
		
		return fieldsGrassMountain;
		
	}
	
	public boolean hasNoIsland(HalfMap mapToVerify) {
		
		this.alreadyVisited = new ArrayList<Coordinates>();
		
		Map<Coordinates, HalfMapNode> fields = new HashMap<Coordinates, HalfMapNode>();
		
		for (HalfMapNode node: mapToVerify.getMapNodes()) {
			
			Coordinates pos = new Coordinates(node.getX(), node.getY());
			fields.put(pos, node);
			
		}
		
		Random randomNo = new Random();
		int randomX = randomNo.nextInt(8);
		int randomY = randomNo.nextInt(4);
		
		Coordinates startingPos = new Coordinates(randomX, randomY);
		
		while(fields.get(startingPos).getTerrain() == ETerrain.Water) {
			randomX = randomNo.nextInt(8);
			randomY = randomNo.nextInt(4);
			startingPos = new Coordinates(randomX, randomY);
		}
		checkIfReachable(startingPos, fields, alreadyVisited);
		
		return (alreadyVisited.size() == this.getGrassMountainFields(mapToVerify).size());
	}
	
	
	
	// CODE TAKEN FROM START https://www.geeksforgeeks.org/flood-fill-algorithm-implement-fill-paint/
	private void checkIfReachable(Coordinates startingPos, Map<Coordinates, HalfMapNode> mapToVerify, List<Coordinates> visitedNodes) {
		
		if(!mapToVerify.containsKey(startingPos) || visitedNodes.contains(startingPos) || mapToVerify.get(startingPos).getTerrain() == ETerrain.Water) {
			return;
		}else {
			visitedNodes.add(startingPos);
			checkIfReachable(new Coordinates(startingPos.getX() - 1, startingPos.getY()), mapToVerify, visitedNodes);
			checkIfReachable(new Coordinates(startingPos.getX() + 1, startingPos.getY()), mapToVerify, visitedNodes);
			checkIfReachable(new Coordinates(startingPos.getX(), startingPos.getY() - 1), mapToVerify, visitedNodes);
			checkIfReachable(new Coordinates(startingPos.getX(), startingPos.getY() + 1), mapToVerify, visitedNodes);
		}
		
	}

	@Override
	public void validatePlayerReg(Map<String, GameState> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void validatePlayerId(Map<String, GameState> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {
		
	}
	
	// if game exists
	@Override
	public void validateGameId(Map<String, GameState> games, UniqueGameIdentifier gameId) {
		
	}

	@Override
	public void validateHalfMap(HalfMap halfMap) {
		if (hasNoIsland(halfMap) == false) {
			throw new HalfMapException("Island found", "Half Map contains island(s)");
		}
		
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
