package client.models.mapData;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.models.mapData.enums.MapFieldType;

public class ClientMap {
	
	private Map<Coordinates, MapField> fields;
	
	private String gameID;
	
	private int xSize;

	private int ySize;
	private static final Logger logger = LoggerFactory.getLogger(ClientMap.class);

	
	
	public ClientMap() {
		super();
	}
	
	
	
	public ClientMap(Map<Coordinates, MapField> fields) {
		super();
		this.fields = fields;
		int maxX = 0;
		int maxY = 0;
		for( Map.Entry<Coordinates, MapField> mapEntry : fields.entrySet() ) {
			if (mapEntry.getValue().getPosition().getX() > maxX) {
				maxX = mapEntry.getValue().getPosition().getX();
			}
			if (mapEntry.getValue().getPosition().getY() > maxY) {
				maxY = mapEntry.getValue().getPosition().getY();
			}
		}
		this.xSize = maxX;
		this.ySize = maxY;
	}



	public ClientMap(ClientMap myMap) {
		this.fields = myMap.fields;
		this.xSize = myMap.xSize;
		this.ySize = myMap.ySize;
	}

	public ClientMap(Map<Coordinates, MapField> fields, String gameID, int xSize, int ySize) {
		super();
		this.fields = fields;
		this.gameID = gameID;
		this.xSize = xSize;
		this.ySize = ySize;
	}

	public Map<Coordinates, MapField> getFields() {
		return fields;
	}

	public void setFields(Map<Coordinates, MapField> fields) {
		this.fields = fields;
	}

	public int getxSize() {
		return xSize;
	}

	public void setxSize(int xSize) {
		this.xSize = xSize;
	}

	public int getySize() {
		return ySize;
	}

	public void setySize(int ySize) {
		this.ySize = ySize;
	}
	
	//reachable nodes (in theory)
	public Map<Coordinates, MapField> getVisitableNodes() {
		
		Map<Coordinates, MapField> fieldsGrassMountain = new HashMap<Coordinates, MapField>();
		
		logger.info("Client Map getVisitableNodes");
		for( Map.Entry<Coordinates, MapField> mapEntry : this.getFields().entrySet() ) {
			if (mapEntry.getValue().getType() == MapFieldType.GRASS || mapEntry.getValue().getType() == MapFieldType.MOUNTAIN) {
				fieldsGrassMountain.put(mapEntry.getKey(), mapEntry.getValue());
			}
		}
		
		return fieldsGrassMountain;
		
	}
	
	/*private Map<Coordinates, MapField> mapWithoutWaters () {
		
		Map<Coordinates, MapField> toReturn = new HashMap<Coordinates, MapField>();
		
		for( Map.Entry<Coordinates, MapField> mapEntry : fields.entrySet() ) {
			
			if (mapEntry.getValue().getType() != MapFieldType.WATER) {
				toReturn.put(mapEntry.getKey(), mapEntry.getValue());
			}
		}
		
		return toReturn;
		
	}
	*/
	
}
