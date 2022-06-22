package server.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import server.controllers.GameStateController;
import server.enums.MoveCommand;
import server.exceptions.HalfMapException;
import server.exceptions.NotEnoughPlayersException;
import server.models.Coordinates;
import server.models.GameData;
import server.models.InternalHalfMap;
import server.models.MapNode;
import server.models.Player;

public class FieldsCoordinatesRule implements IRuleValidation{

	
	public void checkIfValidCoordinates(HalfMap halfMap) {
		for (HalfMapNode node: halfMap.getMapNodes()) {
			if (node.getX() < 0 || node.getY() < 0) {
				throw new HalfMapException("Invalid Node Coordinates", "Position received: [" + node.getX() + " " + node.getY() + "]");
			}
		}
	}
	
	public void checkNeededFields(HalfMap halfMap) {
		List<Coordinates> nodePositions = new ArrayList<Coordinates>();
		
		for (HalfMapNode node: halfMap.getMapNodes()) {
			Coordinates pos = new Coordinates(node.getX(), node.getY());
			nodePositions.add(pos);
		}
		
		for (int y = 0; y < 4; ++y) {
			
			for(int x = 0; x < 8; ++x) {
				
				Coordinates pos = new Coordinates(x, y);
			
				if (nodePositions.contains(pos) == false) {
					throw new HalfMapException("Requested Node Positions Not Found", "Position not found: [" + pos.getX() + " " + pos.getY() + "]");
				}
			}
		}
	}

	@Override
	public void validatePlayerReg(Map<String, GameData> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {}
	
	@Override
	public void validatePlayerId(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {}
	
	@Override
	public void validateGameId(Map<String, GameData> games, UniqueGameIdentifier gameId) {}

	@Override
	public void validateHalfMap(HalfMap halfMap) {
		checkIfValidCoordinates(halfMap);
		checkNeededFields(halfMap);
		
	}

	@Override
	public void validateGameState(GameStateController controller, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {
	
		if (controller.bothPlayersRegistered(gameId) == false) {
			throw new NotEnoughPlayersException("Only one client has registered", "Client tried to send half Map but not both players were registered");
		}
	}

	@Override
	public void validateMove(Map<String, GameData> games, PlayerMove move, UniqueGameIdentifier gameId) {}

	@Override
	public void myTurn(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {}

	
}
