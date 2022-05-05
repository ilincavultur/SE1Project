package client.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import MessagesBase.MessagesFromServer.GameState;
import MessagesBase.MessagesFromServer.PlayerState;
import client.models.gameData.GameStateData;
import client.models.gameData.enums.ClientPlayerState;
import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.models.mapData.enums.FortState;
import client.models.mapData.enums.MapFieldType;
import client.models.mapData.enums.PlayerPositionState;
import client.models.mapData.enums.TreasureState;
import client.movement.PathCalculator;
import client.movement.enums.MoveCommand;

public class NetworkConverter {
	private static final Logger logger = LoggerFactory.getLogger(NetworkConverter.class);

	public ETerrain convertTerrainTypeTo(MapFieldType fieldType) {
		
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
	
	public MapFieldType convertTerrainTypeFrom(ETerrain fieldType) {
		
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
	
	public ClientPlayerState convertPlayerStateFrom(EPlayerGameState plState) {
		
		if(plState == EPlayerGameState.MustAct) {
			return ClientPlayerState.MUSTACT;
		}
		if(plState == EPlayerGameState.MustWait) {
			return ClientPlayerState.MUSTWAIT;		
		}
		if(plState == EPlayerGameState.Won) {
			return ClientPlayerState.WON;
		}
		if(plState == EPlayerGameState.Lost) {
			return ClientPlayerState.LOST;
		}
		return null;
		
	}
	
	public PlayerPositionState convertPlayerPositionStateFrom(EPlayerPositionState plPosState) {
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
	
	public FortState convertFortStateFrom(EFortState fortState) {
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
	
	public TreasureState convertTreasureStateFrom(ETreasureState treasureState) {
		if(treasureState == ETreasureState.MyTreasureIsPresent) {
			return TreasureState.MYTREASURE;
		}
		if(treasureState == ETreasureState.NoOrUnknownTreasureState) {
			return TreasureState.UNKNOWNIFTREASURE;
		}
		return null;
	}
	
	public MapField convertFullMapNodeFrom(FullMapNode fullMapNode) {
		
		MapField toReturn = new MapField();
		
		toReturn.setType(convertTerrainTypeFrom(fullMapNode.getTerrain()));
		toReturn.setPlayerPositionState(convertPlayerPositionStateFrom(fullMapNode.getPlayerPositionState()));
		toReturn.setFortState(convertFortStateFrom(fullMapNode.getFortState()));
		Coordinates pos = new Coordinates(fullMapNode.getX(), fullMapNode.getY());
		toReturn.setPosition(pos);
		toReturn.setTreasureState(convertTreasureStateFrom(fullMapNode.getTreasureState()));
		toReturn.setShortestPath(new ArrayList<Coordinates>());
		
		return toReturn;
		
	}
	
	public ClientMap convertFullMapFrom(FullMap fullMap) {
		
		ClientMap toReturn = new ClientMap();
		Map<Coordinates, MapField> newMp = new HashMap<Coordinates, MapField>();
		Set<FullMapNode> fullMapFields = fullMap.getMapNodes().stream().collect(Collectors.toSet());

		int maxX = 0;
		int maxY = 0;
		
		for (FullMapNode node : fullMapFields) {
			MapField field = new MapField();
			field = convertFullMapNodeFrom(node);
			if(field.getPosition().getX() > maxX) {
				maxX = field.getPosition().getX();
			}
			if(field.getPosition().getY() > maxY) {
				maxY = field.getPosition().getY();
			}
			newMp.put(field.getPosition(), field);
			//toReturn.getFields().put(field.getPosition(), field);
		}
		toReturn.setFields(newMp);
		toReturn.setxSize(maxX + 1);
		toReturn.setySize(maxY + 1);
		
		return toReturn;
		
	}
	
	public GameStateData convertGameStateFrom(GameState gameState) {
		//get map
		GameStateData state = new GameStateData();
		
		state.setGameStateId(gameState.getGameStateId());
		Iterator<PlayerState> it = gameState.getPlayers().iterator();
		
		//state.setPlayerPosition(state.getMyCurrentPosition());
		//state.setPlayerPosition(gameState.getMap().);
		if(it.hasNext()) {
			PlayerState element = it.next();
			state.setPlayerState(convertPlayerStateFrom(element.getState()));
			state.setPlayerId(element.getUniquePlayerID());
			if(element.hasCollectedTreasure()) {
				//TODO
				state.setHasCollectedTreasure(true);
			}
		
			
			
		}
		
		
		if(gameState.getMap().isPresent()) {
			state.setFullMap(convertFullMapFrom(gameState.getMap().get()));	
			for( Map.Entry<Coordinates, MapField> mapEntry : state.getFullMap().getFields().entrySet() ) {
				if (mapEntry.getValue().getPlayerPositionState() == PlayerPositionState.MYPLAYER || mapEntry.getValue().getPlayerPositionState() == PlayerPositionState.BOTH) {
					//logger.info("myplayer or both players");
					state.setPlayerPosition(mapEntry.getKey());
					//System.out.println("acum suntem aici : in converter" + state.getPlayerPosition().getX() + state.getPlayerPosition().getY());
					
				}
				if(mapEntry.getValue().getTreasureState() == TreasureState.MYTREASURE) {
					
					state.setTreasureIsPresentAt(mapEntry.getKey());
				}
				if (mapEntry.getValue().getFortState() == FortState.ENEMYFORT) {
					
					state.setEnemyFortIsPresentAt(mapEntry.getKey());
				}
			}
		} else {
			System.out.println("Full Map not available");
		}
		
		
	
		return state;
		
	}
	
	public HalfMapNode convertMapNodeTo(MapField mapField) {
		
		ETerrain terrain = convertTerrainTypeTo(mapField.getType());
		boolean fortState = false;
		
		if(mapField.getFortState() == FortState.MYFORT) {
			fortState = true;
		}
		
		HalfMapNode toReturn = new HalfMapNode(mapField.getPosition().getX(), mapField.getPosition().getY(), fortState, terrain);
		
		return toReturn;
	}

	public HalfMap convertMapTo(String uniquePlayerID, ClientMap myMap) {
	
		Map<Coordinates, MapField> myMapNodes = myMap.getFields();
		
		Set<HalfMapNode> networkNodes = new HashSet<HalfMapNode>();
		
		
		for (Entry<Coordinates, MapField> node : myMapNodes.entrySet()) {
			
			HalfMapNode nodeToAdd = convertMapNodeTo(node.getValue());
			
			networkNodes.add(nodeToAdd);
			
		}
		
		HalfMap toReturn = new HalfMap(uniquePlayerID, networkNodes);
		
		return toReturn;
		
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
