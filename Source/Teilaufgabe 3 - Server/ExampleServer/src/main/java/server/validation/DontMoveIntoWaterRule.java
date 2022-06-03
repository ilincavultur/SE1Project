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
import server.enums.MapFieldType;
import server.enums.MoveCommand;
import server.exceptions.MoveException;
import server.models.Coordinates;
import server.models.GameState;
import server.models.InternalHalfMap;
import server.models.MapNode;
import server.models.Player;

public class DontMoveIntoWaterRule implements IRuleValidation{

	@Override
	public void validatePlayerReg(Map<String, GameState> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateHalfMap(HalfMap halfMap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateGameState(Map<String, GameState> games, UniquePlayerIdentifier playerId,
			UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateMove(Map<String, GameState> games, PlayerMove move, UniqueGameIdentifier gameId) {
		
		String playerId = move.getUniquePlayerID().toString();
		Player player = games.get(gameId.toString()).getPlayerWithId(playerId);
		GameState game = games.get(gameId.toString());
		Coordinates pos = player.getCurrPos();
		Map<Coordinates, MapNode> fields = game.getFullMap().getFields();
		if (move.getMove() == EMove.Up) {
			Coordinates dir = pos.getUpNeighbour(fields);
			if (fields.get(dir).getFieldType() == MapFieldType.WATER) {
				throw new MoveException ("Move goes to WATER", "Client tried to move to a WATER field: [" + dir.getX() + " " + dir.getY() + "] from field [" + pos.getX()+ " " + pos.getY() +"]"  ); 
			}
		}
		
		if (move.getMove() == EMove.Down) {
			Coordinates dir = pos.getDownNeighbour(fields);
			if (fields.get(dir).getFieldType() == MapFieldType.WATER) {
				throw new MoveException ("Move goes to WATER", "Client tried to move to a WATER field: [" + dir.getX() + " " + dir.getY() + "] from field [" + pos.getX()+ " " + pos.getY() +"]"  ); 
			}
		}
		
		if (move.getMove() == EMove.Left) {
			Coordinates dir = pos.getLeftNeighbour(fields);
			if (fields.get(dir).getFieldType() == MapFieldType.WATER) {
				throw new MoveException ("Move goes to WATER", "Client tried to move to a WATER field: [" + dir.getX() + " " + dir.getY() + "] from field [" + pos.getX()+ " " + pos.getY() +"]"  ); 
			}
		}
		
		if (move.getMove() == EMove.Right) {
			Coordinates dir = pos.getRightNeighbour(fields);
			if (fields.get(dir).getFieldType() == MapFieldType.WATER) {
				throw new MoveException ("Move goes to WATER", "Client tried to move to a WATER field: [" + dir.getX() + " " + dir.getY() + "] from field [" + pos.getX()+ " " + pos.getY() +"]"  ); 
			}
		}
		
	}

	@Override
	public void myTurn(Map<String, GameState> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId) {
		// TODO Auto-generated method stub
		
	}

}
