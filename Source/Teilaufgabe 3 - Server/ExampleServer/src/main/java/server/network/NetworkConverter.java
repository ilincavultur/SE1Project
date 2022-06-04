package server.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.EMove;
import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
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
import server.enums.FortState;
import server.enums.MapFieldType;
import server.enums.MoveCommand;
import server.enums.PlayerPositionState;
import server.enums.ServerPlayerState;
import server.enums.TreasureState;
import server.models.Coordinates;
import server.models.GameData;
import server.models.InternalFullMap;

public class NetworkConverter {

	public InternalHalfMap convertHalfMapFrom(HalfMap halfMap) {
		
		InternalHalfMap toReturn = new InternalHalfMap();
		Map<Coordinates, MapNode> newMp = new HashMap<Coordinates, MapNode>();
		Set<HalfMapNode> halfMapFields = halfMap.getMapNodes().stream().collect(Collectors.toSet());

		//int maxX = 0;
		//int maxY = 0;
		
		for (HalfMapNode node : halfMapFields) {
			MapNode field = new MapNode();
			field = convertMapNodeFrom(node);
			/*if(node.getX() > maxX) {
				maxX = node.getX();
			}
			if(node.getY() > maxY) {
				maxY = node.getY();
			}*/
			
			if (node.isFortPresent()) {
				Coordinates fortPos = new Coordinates(node.getX(), node.getY());
				toReturn.setFortPos(fortPos);
			}
			Coordinates pos = new Coordinates(node.getX(), node.getY());
			newMp.put(pos, field);
		}
		toReturn.setFields(newMp);
		//toReturn.setxSize(maxX + 1);
		//toReturn.setySize(maxY + 1);
		
		return toReturn;
		
	}
	
	// enemy flag is true if the player is an enemy
	public PlayerState convertPlayerFrom(GameData game, Player player, boolean enemy) {
		
		PlayerState toReturn = new PlayerState();
		if (enemy) {
			String firstName = "";
			String lastName = "";
			String uaccount = "";
			EPlayerGameState state = EPlayerGameState.MustWait;
			if (game.myTurn(player.getPlayerId())) {
				state = EPlayerGameState.MustAct;
			} else {
				state = EPlayerGameState.MustWait;
			} 
			if (game.getWinnerId() != null) {
				if (game.getWinnerId().equals(player.getPlayerId())) {
					state = EPlayerGameState.Won;	
				} else {
					state = EPlayerGameState.Lost;
				}		
			} 
			UniquePlayerIdentifier identifier = new UniquePlayerIdentifier(player.getPlayerId());
			boolean collectedTreasure = false;
			return new PlayerState(firstName, lastName, uaccount, state, identifier, collectedTreasure);
		}
		String firstName = "";
		String lastName = "";
		String uaccount = "";
		EPlayerGameState state = EPlayerGameState.MustWait;
		if (game.myTurn(player.getPlayerId())) {
			state = EPlayerGameState.MustAct;
		} else {
			state = EPlayerGameState.MustWait;
		} 
		
		if (game.getWinnerId() != null) {
			if (game.getWinnerId().equals(player.getPlayerId())) {
				state = EPlayerGameState.Won;	
			} else {
				state = EPlayerGameState.Lost;
			}		
		} 
		
		UniquePlayerIdentifier identifier = new UniquePlayerIdentifier(player.getPlayerId());
		boolean collectedTreasure = player.isHasCollectedTreasure();
		return new PlayerState(firstName, lastName, uaccount, state, identifier, collectedTreasure);
	
		
	}
	
	private MapNode convertMapNodeFrom(HalfMapNode node) {
		
		MapNode toReturn = new MapNode();
		
		toReturn.setFieldType(convertTerrainTypeFrom(node.getTerrain()));
		//toReturn.setPlayerPositionState(convertPlayerPositionStateFrom(node.ge));
		
		Coordinates pos = new Coordinates(node.getX(), node.getY());
		toReturn.setPosition(pos);
		//toReturn.setTreasureState(convertTreasureStateFrom(node.getTreasureState()));
		
		
		return toReturn;
		
	}
	

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
		return null;
	}
	
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
		return null;
	}
	
	private ServerPlayerState convertPlayerStateFrom(EPlayerGameState plState) {
		
		if(plState == EPlayerGameState.MustAct) {
			return ServerPlayerState.MUSTACT;
		}
		if(plState == EPlayerGameState.MustWait) {
			return ServerPlayerState.MUSTWAIT;		
		}
		if(plState == EPlayerGameState.Won) {
			return ServerPlayerState.WON;
		}
		if(plState == EPlayerGameState.Lost) {
			return ServerPlayerState.LOST;
		}
		return null;
		
	}
	
	private PlayerPositionState convertPlayerPositionStateFrom(EPlayerPositionState plPosState) {
		
		if(plPosState == EPlayerPositionState.NoPlayerPresent) {
			return PlayerPositionState.NOPLAYER;
		}
		if(plPosState == EPlayerPositionState.EnemyPlayerPosition) {
			return PlayerPositionState.ENEMYPLAYER;
		}
		if(plPosState == EPlayerPositionState.MyPlayerPosition) {
			return PlayerPositionState.MYPLAYER;
		}
		if(plPosState == EPlayerPositionState.BothPlayerPosition) {
			return PlayerPositionState.BOTH;
		}
		return null;
	}
	
	private FortState convertFortStateFrom(EFortState fortState) {
		
		if(fortState == EFortState.EnemyFortPresent) {
			return FortState.ENEMYFORT;
		}
		if(fortState == EFortState.MyFortPresent) {
			return FortState.MYFORT;
		}
		if(fortState == EFortState.NoOrUnknownFortState) {
			return FortState.UNKNOWNIFFORT;
		}
		
		return null;
		
	}
	
	private TreasureState convertTreasureStateFrom(ETreasureState treasureState) {
		
		if(treasureState == ETreasureState.MyTreasureIsPresent) {
			return TreasureState.MYTREASURE;
		}
		if(treasureState == ETreasureState.NoOrUnknownTreasureState) {
			return TreasureState.UNKNOWNIFTREASURE;
		}
		return null;
	}
	
	private FullMapNode convertMapNodeTo(MapNode node) {
		
		FullMapNode toReturn = new FullMapNode();
		
		ETerrain fieldType = convertTerrainTypeTo(node.getFieldType());
		//toReturn.setPlayerPositionState(convertPlayerPositionStateFrom(node.ge));
		
		Coordinates pos = node.getPosition();
		
		//toReturn.setTreasureState(convertTreasureStateFrom(node.getTreasureState()));
		
		
		return toReturn;
		
	}
	
	// server full map to network fullmap
	public Optional<FullMap> convertServerFullMapTo(Player myPlayer, Player enemyPlayer, InternalFullMap myMap, GameData gameState) {
		
		if (myMap == null) {
			return Optional.empty();
		}
		//TODO
		FullMap toRet = new FullMap();
		Set<FullMapNode> mapNodes = new HashSet<FullMapNode>();
		
		
		for( Map.Entry<Coordinates, MapNode> mapEntry : myMap.getFields().entrySet() ) {
			//FullMapNode toReturn = new FullMapNode();
			
			ETerrain fieldType = convertTerrainTypeTo(mapEntry.getValue().getFieldType());
			//toReturn.setPlayerPositionState(convertPlayerPositionStateFrom(node.ge));
			
			Coordinates pos = mapEntry.getKey();
			//de aici lucreaza
			ETreasureState treasure = ETreasureState.NoOrUnknownTreasureState;
			EFortState fort = EFortState.NoOrUnknownFortState;
			EPlayerPositionState playerPos = EPlayerPositionState.NoPlayerPresent;
			
	
			if (myPlayer.getCurrPos().equals(mapEntry.getKey()) && myPlayer.getCurrPos().equals(mapEntry.getKey())) {
				playerPos = EPlayerPositionState.BothPlayerPosition;
			} else if (myPlayer.getCurrPos().equals(mapEntry.getKey())) {
				playerPos = EPlayerPositionState.MyPlayerPosition;
			} else if (enemyPlayer.getCurrPos().equals(mapEntry.getKey())) {
				playerPos = EPlayerPositionState.EnemyPlayerPosition;
			}
			
			FullMapNode fullMapNode = new FullMapNode(fieldType, playerPos, treasure, fort, pos.getX(), pos.getY());
			mapNodes.add(fullMapNode);
			
			
		}
	
		toRet = new FullMap(mapNodes);
		return Optional.of(toRet);
		
	}
	
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
		return null;
	}
	
	
}
