package server.validation;

import java.util.Map;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.EMove;
import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import server.controllers.GameStateController;
import server.enums.MapFieldType;
import server.enums.MoveCommand;
import server.exceptions.MoveException;
import server.exceptions.NotEnoughPlayersException;
import server.models.Coordinates;
import server.models.GameData;
import server.models.InternalHalfMap;
import server.models.MapNode;
import server.models.Player;

public class DontMoveIntoWaterRule implements IRuleValidation{

	@Override
	public void validatePlayerReg(Map<String, GameData> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {}
	
	@Override
	public void validatePlayerId(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {}
	
	@Override
	public void validateGameId(Map<String, GameData> games, UniqueGameIdentifier gameId) {}

	@Override
	public void validateHalfMap(HalfMap halfMap) {}

	@Override
	public void validateGameState(GameStateController controller, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {}

	@Override
	public void validateMove(Map<String, GameData> games, PlayerMove move, UniqueGameIdentifier gameId) {
		
		String playerId = move.getUniquePlayerID();
		Player player = games.get(gameId.getUniqueGameID()).getPlayerWithId(playerId);
		GameData game = games.get(gameId.getUniqueGameID());
		Coordinates pos = player.getCurrPos();
		Map<Coordinates, MapNode> fields = game.getFullMap().getFields();
		
		if (move.getMove() == EMove.Up) {
			Coordinates dir = pos.getUpNeighbour(fields);
			if (fields.containsKey(dir)) {
				if (fields.get(dir).getFieldType() == MapFieldType.WATER) {
					throw new MoveException ("Move goes to WATER", "Client tried to move to a WATER field: [" + dir.getX() + " " + dir.getY() + "] from field [" + pos.getX()+ " " + pos.getY() +"]"  ); 
				}
			} else {
				throw new MoveException ("Move goes outside map", "Client tried to move to a non existing field: [" + dir.getX() + " " + dir.getY() + "] from field [" + pos.getX()+ " " + pos.getY() +"]"  );
			}
		}
		
		if (move.getMove() == EMove.Down) {
			Coordinates dir = pos.getDownNeighbour(fields);
			if (fields.containsKey(dir)) {
				if (fields.get(dir).getFieldType() == MapFieldType.WATER) {
					throw new MoveException ("Move goes to WATER", "Client tried to move to a WATER field: [" + dir.getX() + " " + dir.getY() + "] from field [" + pos.getX()+ " " + pos.getY() +"]"  ); 
				}
			} else {
				throw new MoveException ("Move goes outside map", "Client tried to move to a non existing field: [" + dir.getX() + " " + dir.getY() + "] from field [" + pos.getX()+ " " + pos.getY() +"]"  );
			}
		}
		
		if (move.getMove() == EMove.Left) {
			Coordinates dir = pos.getLeftNeighbour(fields);
			if (fields.containsKey(dir)) {
				if (fields.get(dir).getFieldType() == MapFieldType.WATER) {
					throw new MoveException ("Move goes to WATER", "Client tried to move to a WATER field: [" + dir.getX() + " " + dir.getY() + "] from field [" + pos.getX()+ " " + pos.getY() +"]"  ); 
				} 
			} else {
				throw new MoveException ("Move goes outside map", "Client tried to move to a non existing field: [" + dir.getX() + " " + dir.getY() + "] from field [" + pos.getX()+ " " + pos.getY() +"]"  );
			}
		}
		
		if (move.getMove() == EMove.Right) {
			Coordinates dir = pos.getRightNeighbour(fields);
			if (fields.containsKey(dir)) {
				if (fields.get(dir).getFieldType() == MapFieldType.WATER) {
					throw new MoveException ("Move goes to WATER", "Client tried to move to a WATER field: [" + dir.getX() + " " + dir.getY() + "] from field [" + pos.getX()+ " " + pos.getY() +"]"  ); 
				} 
			} else {
				throw new MoveException ("Move goes outside map", "Client tried to move to a non existing field: [" + dir.getX() + " " + dir.getY() + "] from field [" + pos.getX()+ " " + pos.getY() +"]"  );
			}
		}
		
	}

	@Override
	public void myTurn(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {}

}
