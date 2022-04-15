package client.network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromServer.EPlayerGameState;
import MessagesBase.MessagesFromServer.GameState;
import MessagesBase.MessagesFromServer.PlayerState;
import client.models.gameData.GameStateData;
import client.models.gameData.enums.ClientPlayerState;
import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.models.mapData.enums.FortState;
import client.models.mapData.enums.MapFieldType;

public class NetworkConverter {
	
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
	
	public GameStateData convertGameStateFrom(GameState gameState) {
		//get map
		GameStateData state = new GameStateData();
		
		state.setGameStateId(gameState.getGameStateId());
		Iterator<PlayerState> it = gameState.getPlayers().iterator();
		
		if(it.hasNext()) {
			PlayerState element = it.next();
			state.setPlayerState(convertPlayerStateFrom(element.getState()));
			state.setPlayerId(element.getUniquePlayerID());
		}
		
		state.setHasCollectedTreasure(false);
	
		return state;
		
	}
	
	public HalfMapNode convertMapNodeTo(MapField mapField) {
		
		ETerrain terrain = convertTerrainTypeTo(mapField.getType());
		boolean fortState = false;
		
		if(mapField.getFortState() == FortState.ENEMYFORT || mapField.getFortState() == FortState.MYFORT) {
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
	
	
	
}
