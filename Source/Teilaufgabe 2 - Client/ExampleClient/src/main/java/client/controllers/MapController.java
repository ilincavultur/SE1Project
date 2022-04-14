package client.controllers;

import client.models.mapData.ClientMap;
import client.models.mapData.ClientMapGenerator;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.models.mapData.MapValidator;
import client.models.mapData.enums.FortState;
import client.models.mapData.enums.MapFieldType;
import client.models.mapData.enums.PlayerPositionState;
import client.models.mapData.enums.TreasureState;

public class MapController {
	
	ClientMap myMap;
	ClientMapGenerator myMapGenerator;
	MapValidator validator;

	public MapController() {
		super();
		this.myMap = new ClientMap();
		this.myMapGenerator = new ClientMapGenerator();
		this.validator = new MapValidator();
	}

	public MapController(ClientMap myMap, MapValidator validator) {
		super();
		this.myMap = myMap;
		this.validator = validator;
	}

	public void generateMap() {

		myMapGenerator.createMap();
		
		ClientMap myNewMap = new ClientMap(myMapGenerator.getFields());		
		
		if(validator.validateMap(myNewMap)) {
			
			this.myMap = myNewMap;
			
		} else {
			
			System.out.println("NU E OK");
			// TO DO

		}
		
	}
	
	public ClientMap getMyMap() {
		return myMap;
	}

	public void setMyMap(ClientMap myMap) {
		this.myMap = myMap;
	}
	
	
	public MapField getField(Coordinates pos) {

		return this.myMap.getFields().get(pos);
		
	}
	
	public Coordinates placeFort() {
		
		// TO DO
		return null;
		
	}
	
	
	
	

}
