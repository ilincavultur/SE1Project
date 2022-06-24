package server.network;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.EMove;
import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromServer.EFortState;
import MessagesBase.MessagesFromServer.EPlayerGameState;
import MessagesBase.MessagesFromServer.EPlayerPositionState;
import MessagesBase.MessagesFromServer.ETreasureState;
import MessagesBase.MessagesFromServer.FullMap;
import MessagesBase.MessagesFromServer.FullMapNode;
import MessagesBase.MessagesFromServer.PlayerState;
import server.models.InternalHalfMap;
import server.models.MapNode;
import server.models.Player;
import server.controllers.FullMapHandler;
import server.enums.FortState;
import server.enums.MapFieldType;
import server.enums.MoveCommand;
import server.enums.PlayerPositionState;
import server.enums.ServerPlayerState;
import server.enums.TreasureState;
import server.models.Coordinates;
import server.models.GameData;

public class NetworkConverter {
	
	private static final Logger logger = LoggerFactory.getLogger(NetworkConverter.class);

	/*
	 * Convert Network object HalfMap to internal object InternalHalfMap
	 */
	public InternalHalfMap convertHalfMapFrom(HalfMap halfMap) {
		
		InternalHalfMap toReturn = new InternalHalfMap();
		Map<Coordinates, MapNode> newMp = new HashMap<Coordinates, MapNode>();
		Set<HalfMapNode> halfMapFields = halfMap.getMapNodes().stream().collect(Collectors.toSet());

		for (HalfMapNode node : halfMapFields) {
			
			Coordinates pos = new Coordinates(node.getX(), node.getY());
			MapNode field = convertMapNodeFrom(node);
			
			if (node.isFortPresent()) {
				Coordinates fortPos = new Coordinates(node.getX(), node.getY());
				toReturn.setFortPos(fortPos);
			}
			
			newMp.put(pos, field);
		}
	
		toReturn.setFields(newMp);

		return toReturn;
		
	}
	
	/*
	 *  Enemy flag is true if the player is an enemy
	 *  Convert internal Player State to Network Object PlayerState
	 */
	public PlayerState convertPlayerTo(GameData game, Player player, boolean enemy) {
		
		String firstName = player.getPlayerReg().getStudentFirstName();
		String lastName = player.getPlayerReg().getStudentLastName();
		String uaccount = player.getPlayerReg().getStudentUAccount();
		EPlayerGameState state = EPlayerGameState.MustWait;
		
		if (game.getWinnerId().isEmpty() == false) {
			if (game.getWinnerId().equals(player.getPlayerId())) {
				state = EPlayerGameState.Won;	
			} else {
				state = EPlayerGameState.Lost;
			}		
		} else {
			if (game.myTurn(player.getPlayerId())) {
				state = EPlayerGameState.MustAct;
			} else {
				state = EPlayerGameState.MustWait;
			} 
		}
		
		UniquePlayerIdentifier identifier = new UniquePlayerIdentifier();
		
		if (enemy) {
			identifier = new UniquePlayerIdentifier(UUID.randomUUID().toString());
		} else {
			identifier = new UniquePlayerIdentifier(player.getPlayerId());
		}

		boolean collectedTreasure = player.isHasCollectedTreasure();
		return new PlayerState(firstName, lastName, uaccount, state, identifier, collectedTreasure);
	
	}
	
	/*
	 *  Convert Network Object HalfMapNode to internal object MapNode
	 */
	private MapNode convertMapNodeFrom(HalfMapNode node) {
		
		Coordinates pos = new Coordinates(node.getX(), node.getY());
		return new MapNode(pos, convertTerrainTypeFrom(node.getTerrain()));	
		
	}
	
	/*
	 *  Convert internal MapFieldType to Network object ETerrain
	 */
	private ETerrain convertTerrainTypeTo(MapFieldType fieldType) {
		
		if(fieldType == MapFieldType.GRASS) {
			return ETerrain.Grass;
		}
		
		if(fieldType == MapFieldType.MOUNTAIN) {
			return ETerrain.Mountain;
		}
		
		if(fieldType == MapFieldType.WATER) {
			return ETerrain.Water;
		}
		return ETerrain.Grass;
	}
	
	/*
	 *  Convert Network Object ETerrain to internal object MapFieldType
	 */
	private MapFieldType convertTerrainTypeFrom(ETerrain fieldType) {
		
		if(fieldType == ETerrain.Grass) {
			return MapFieldType.GRASS;
		}
		
		if(fieldType == ETerrain.Mountain) {
			return MapFieldType.MOUNTAIN;
		}
		
		if(fieldType == ETerrain.Water) {
			return MapFieldType.WATER;
		}
		return MapFieldType.GRASS;
	}

	/*
	 *  Convert internal FullMap to Network Optional FullMap
	 */
	public Optional<FullMap> convertServerFullMapTo(UniquePlayerIdentifier playerID, GameData game) {
		
		FullMap toReturn = new FullMap();

		int roundNo = game.getRoundNo();
		
		FullMapHandler myMap = game.getFullMap();
		Player myPlayer = game.getPlayerWithId(playerID.getUniquePlayerID());
		Player enemyPlayer = game.getTheOtherPlayer(playerID.getUniquePlayerID());
		
		Set<FullMapNode> mapNodes = new HashSet<FullMapNode>();

		Coordinates randomEnemyPosition = myMap.getRandomEnemyPos();
		Coordinates myFortPos = myPlayer.getFortPos();
		Coordinates enemyFortPos = enemyPlayer.getFortPos();
		Coordinates actualEnemyPosition = enemyPlayer.getCurrPos();
		Coordinates myTreasure = myPlayer.getTreasurePos();


		for( var eachNode : myMap.getFields().entrySet() ) {
		
			ETerrain fieldType = convertTerrainTypeTo(eachNode.getValue().getFieldType());
		
			Coordinates pos = eachNode.getKey();
			
			ETreasureState treasure = ETreasureState.NoOrUnknownTreasureState;
			EFortState fort = EFortState.NoOrUnknownFortState;
			EPlayerPositionState playerPos = EPlayerPositionState.NoPlayerPresent;
			
			if (eachNode.getKey().equals(myFortPos)) {
				fort = EFortState.MyFortPresent;
			}
			
			if (eachNode.getKey().equals(enemyFortPos)) {
				if (myPlayer.isShowEnemyFort()) {
					fort = EFortState.EnemyFortPresent;
				}
			} 
			
			if (myPlayer.isHasCollectedTreasure() || myPlayer.isShowTreasure() == false) {
				treasure = ETreasureState.NoOrUnknownTreasureState;
			} else if (eachNode.getKey().equals(myTreasure)) {
				if (myPlayer.isShowTreasure() == true) {
					treasure = ETreasureState.MyTreasureIsPresent;	
				} else {
					treasure = ETreasureState.NoOrUnknownTreasureState;
				}
			}
			
			if (roundNo > 10) {
				
				if (myPlayer.getCurrPos().equals(eachNode.getKey()) && enemyPlayer.getCurrPos().equals(eachNode.getKey())) {
					playerPos = EPlayerPositionState.BothPlayerPosition;	
				} else if (myPlayer.getCurrPos().equals(eachNode.getKey())) {
					playerPos = EPlayerPositionState.MyPlayerPosition;	
				} else if (eachNode.getKey().equals(actualEnemyPosition)) {
					playerPos = EPlayerPositionState.EnemyPlayerPosition;
				}
			
			} else {
				
				if (myPlayer.getCurrPos().equals(eachNode.getKey()) && randomEnemyPosition.equals(eachNode.getKey())) {
					playerPos = EPlayerPositionState.BothPlayerPosition;
				} else if (myPlayer.getCurrPos().equals(eachNode.getKey())) {
					playerPos = EPlayerPositionState.MyPlayerPosition;
				} else if (eachNode.getKey().equals(randomEnemyPosition)) {
					playerPos = EPlayerPositionState.EnemyPlayerPosition;	
				}
			}
			
			FullMapNode fullMapNode = new FullMapNode(fieldType, playerPos, treasure, fort, pos.getX(), pos.getY());
			mapNodes.add(fullMapNode);
			
		}

		toReturn = new FullMap(mapNodes);
		return Optional.of(toReturn);
		
	}
	
	/*
	 *  Convert internal InternalHalfMap to Network Object Optional FullMap
	 */
	public Optional<FullMap> convertIHalfMapToNetworkFullMap(Player player, GameData gameState) {

		FullMap toReturn = new FullMap();
		
		Set<FullMapNode> mapNodes = new HashSet<FullMapNode>();
		Optional<InternalHalfMap> myMap = player.getHalfMap();
		Coordinates myFortPos = myMap.get().getFortPos();
		
		for( var eachNode : myMap.get().getFields().entrySet() ) {
			
			ETerrain fieldType = convertTerrainTypeTo(eachNode.getValue().getFieldType());
			
			Coordinates pos = eachNode.getKey();
			
			ETreasureState treasure = ETreasureState.NoOrUnknownTreasureState;
			EFortState fort = EFortState.NoOrUnknownFortState;
			EPlayerPositionState playerPos = EPlayerPositionState.NoPlayerPresent;
			
			if (myFortPos.equals(eachNode.getKey())) {
				fort = EFortState.MyFortPresent;
			}
		
			if (player.getCurrPos().equals(eachNode.getKey())) {
				playerPos = EPlayerPositionState.MyPlayerPosition;
			} 
		
			FullMapNode fullMapNode = new FullMapNode(fieldType, playerPos, treasure, fort, pos.getX(), pos.getY());
			mapNodes.add(fullMapNode);
			
		}
	
		toReturn = new FullMap(mapNodes);
		return Optional.of(toReturn);
		
	}
	
	/*
	 *  Convert Network Object PlayerMove to internal object MoveCommand
	 */
	public MoveCommand convertMoveFrom(PlayerMove move) {
		if (move.getMove() == EMove.Up) {
			return MoveCommand.UP;
		}
		
		if (move.getMove() == EMove.Down) {
			return MoveCommand.DOWN;
		}
		
		if (move.getMove() == EMove.Left) {
			return MoveCommand.LEFT;
		}
		
		if (move.getMove() == EMove.Right) {
			return MoveCommand.RIGHT;
		}
		return MoveCommand.DOWN;
	}
	
	/*
	 *  Convert internal MoveCommand to Network object PlayerMove
	 */
	public PlayerMove convertMoveTo(String uniquePlayerID, MoveCommand move) {
		if (move == MoveCommand.UP) {
			return PlayerMove.of(uniquePlayerID, EMove.Up);
		}
		
		if (move == MoveCommand.DOWN) {
			return PlayerMove.of(uniquePlayerID, EMove.Down);
		}
		
		if (move == MoveCommand.LEFT) {
			return PlayerMove.of(uniquePlayerID, EMove.Left);
		}
		
		if (move == MoveCommand.RIGHT) {
			return PlayerMove.of(uniquePlayerID, EMove.Right);
		}
		return PlayerMove.of(uniquePlayerID, EMove.Down);
	}
	
	
}
