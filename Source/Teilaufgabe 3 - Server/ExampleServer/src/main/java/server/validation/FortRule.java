package server.validation;

import java.util.Map;
import java.util.Map.Entry;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import server.controllers.GameStateController;
import server.enums.MapFieldType;
import server.enums.MoveCommand;
import server.exceptions.HalfMapException;
import server.exceptions.NotEnoughPlayersException;
import server.models.InternalHalfMap;
import server.models.MapNode;
import server.models.Player;
import server.models.Coordinates;
import server.models.GameData;

public class FortRule implements IRuleValidation{

	HalfMapNode fortPos = new HalfMapNode();
	
	public void hasOneFort(HalfMap halfMap) {
		int number = 0;
		for (HalfMapNode node: halfMap.getMapNodes()) {
			if (node.isFortPresent()) {
				number += 1;
				fortPos = node;
			}
		}
		if (number != 1) {
			throw new HalfMapException("Invalid Fort Number", "One Fort needed, but found: " + number);
		}
	}

	public void isFortOnGrass(HalfMapNode node) {
	
		if (node.getTerrain() != ETerrain.Grass) {
			throw new HalfMapException("Invalid Fort Terrain Type", "Grass wanted, but found: " + node.getTerrain().toString());
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
		hasOneFort(halfMap);
		isFortOnGrass(fortPos);
		
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
