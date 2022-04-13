package client.network;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
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
	
	public HalfMapNode convertMapNodeTo(MapField mapField) {
		
		ETerrain terrain = convertTerrainTypeTo(mapField.getType());
		boolean fortState = false;

		if (mapField.getFortState() == FortState.MYFORT) {
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
