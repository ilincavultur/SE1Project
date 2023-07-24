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
import server.enums.MapFieldType;
import server.enums.MoveCommand;
import server.exceptions.HalfMapException;
import server.exceptions.NotEnoughPlayersException;
import server.game.GameData;
import server.game.GameStateController;
import server.map.Coordinates;
import server.map.InternalHalfMap;
import server.map.MapNode;
import server.player.Player;

public class TerrainsNumberRule implements IRuleValidation{

	@Override
	public void validatePlayerReg(Map<String, GameData> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {}
	
	@Override
	public void validatePlayerId(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {}

	@Override
	public void validateGameId(Map<String, GameData> games, UniqueGameIdentifier gameId) {}

	@Override
	public void validateHalfMap(HalfMap halfMap) {
		int waterFields = 0;
		int grassFields = 0;
		int mountainFields = 0;
		
		for(HalfMapNode node: halfMap.getMapNodes()) {
			if (node.getTerrain() == ETerrain.Grass) {
				grassFields+=1;
			}
			if (node.getTerrain() == ETerrain.Mountain) {
				mountainFields+=1;
			}
			if (node.getTerrain() == ETerrain.Water) {
				waterFields+=1;
			}
		}
		
		if (grassFields < 15) {
			throw new HalfMapException("Invalid Grass Fields Number", "At least 15 Fields needed, but found: " + grassFields);
		}
		
		if (mountainFields < 3) {
			throw new HalfMapException("Invalid Mountain Fields Number", "At least 3 Fields needed, but found: " + mountainFields);
		}
		
		if (waterFields < 4) {
			throw new HalfMapException("Invalid Water Fields Number", "At least 4 Fields needed, but found: " + waterFields);
		}
		
	}

	@Override
	public void validateGameState(GameStateController controller, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {}

	@Override
	public void validateMove(Map<String, GameData> games, PlayerMove move, UniqueGameIdentifier gameId) {}

	@Override
	public void myTurn(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {}


}
