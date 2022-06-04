package server.validation;

import java.util.Map;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import server.enums.MoveCommand;
import server.models.Coordinates;
import server.models.GameData;
import server.models.InternalHalfMap;
import server.models.MapNode;
import server.models.Player;

public interface IRuleValidation {

	//public void validatePlayerReg(String playerId, String gameId, Map<String, GameState> games);
	// aici trebuie sa fie deja salvate jocurile
	
	// check if no more than 2 players are being registered
	public void validatePlayerReg(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId);
	
	// if player exists
	public void validatePlayerId(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId);
	
	// if game exists
	public void validateGameId(Map<String, GameData> games, UniqueGameIdentifier gameId);
	
	// public void validateHalfMap(InternalHalfMap halfMap);
	// aici trebuie sa fie deja jocurile salvate
	// aici trebuie sa fie deja playersii inregistrati
	// trebuie sa fie randul playerului respectiv ca sa trimita harta
	public void validateHalfMap(HalfMap halfMap);
	
	// public void validateGameState(String gameId, Map<String, GameState> games);
	// aici trebuie sa fie deja jocurile salvate
	// aici trebuie sa fie deja playersii inregistrati
	public void validateGameState(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId);
	
	// gandeste te la asta
	//public void validateMove(Player player, MoveCommand move, Map<Coordinates, MapNode> fields);
	// public void validateHalfMap(InternalHalfMap halfMap);
	// aici trebuie sa fie deja jocurile salvate
	// aici trebuie sa fie deja playersii inregistrati
	// harta trebuie sa fie deja full map 
	// trebuie sa fie randul playerului respectiv ca sa trimita move
	public void validateMove(Map<String, GameData> games, PlayerMove move, UniqueGameIdentifier gameId);
	
	public void myTurn(Map<String, GameData> games, UniquePlayerIdentifier playerId, UniqueGameIdentifier gameId);
}
