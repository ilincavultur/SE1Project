package client.models.mapData;

import java.util.HashMap;
import java.util.Map;

public class ClientMapGenerator {
	
	Map<Coordinates, MapField> fields;
	
	

	public Map<Coordinates, MapField> getFields() {
		return fields;
	}



	public void setFields(Map<Coordinates, MapField> fields) {
		this.fields = fields;
	}



	public void createMap() {
		
		Map<Coordinates, MapField> newfields = new HashMap<Coordinates, MapField>();
		
		
		for (int i = 0 ; i < 7; i++) {
			for(int j = 0; j < 3; j++) {
				MapField newField = new MapField();
				Coordinates pos = new Coordinates(i, j);
				newField = newField.createMapField(pos);
				newfields.put(pos, newField);
			}
		}
		
		this.fields = newfields;
	
		
	}

	
	
	
	
}
