package client.models.mapData;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import client.models.mapData.enums.MapFieldType;

public class ClientMapGenerator {
	
	private Map<Coordinates, MapField> fields;
	
	public Map<Coordinates, MapField> getFields() {
		return fields;
	}

	public void setFields(Map<Coordinates, MapField> fields) {
		this.fields = fields;
	}

	public void createMap() {
		
		Map<Coordinates, MapField> newfields = new HashMap<Coordinates, MapField>();
		
		Random randomNo = new Random();
		
		// initialize map with only grass fields => grass fields min number already accomplished
		
		for(int y=0; y<4; y++) {
		
			for (int x = 0 ; x < 8; x++) {
				
				MapField newField = new MapField();
				Coordinates pos = new Coordinates(x, y);
				newField = newField.createMapField(pos);
				newfields.put(pos, newField);
			}
		}

		for (int i=0; i<4 ; i++) {
			
			int randomWaterX = randomNo.nextInt(8);
			int randomWaterY = randomNo.nextInt(4);
			Coordinates pos = new Coordinates(randomWaterX, randomWaterY);

			if(newfields.get(pos).getType() == MapFieldType.GRASS) {
				
				newfields.get(pos).setType(MapFieldType.WATER);	
			} else {
				--i;
			}
			
		}
		
		for (int i=0; i<3 ; i++) {
			int randomMountainX = randomNo.nextInt(8);
			int randomMountainY = randomNo.nextInt(4);
			Coordinates pos = new Coordinates(randomMountainX, randomMountainY);
			if(newfields.get(pos).getType() == MapFieldType.GRASS) {
				newfields.get(pos).setType(MapFieldType.MOUNTAIN);
			} else {
				--i;
			}
		}
		
		this.fields = newfields;
	
		
	}
	

	public Coordinates placeFort() {
		Random randomNo = new Random();
		
		int randomFortX = randomNo.nextInt(8);
		int randomFortY = randomNo.nextInt(4);
		
		Coordinates fortPos = new Coordinates(randomFortX, randomFortY);
		
		while(fields.get(fortPos).getType() != MapFieldType.GRASS) {
			randomFortX = randomNo.nextInt(8);
			randomFortY = randomNo.nextInt(4);
			fortPos = new Coordinates(randomFortX, randomFortY);
		}
		return fortPos;
		
	}
	
	
	
	
}
