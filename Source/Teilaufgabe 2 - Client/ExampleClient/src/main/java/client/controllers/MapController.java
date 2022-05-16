package client.controllers;

import java.util.Map;
import java.util.Map.Entry;

import client.models.mapData.ClientMap;
import client.models.mapData.ClientMapGenerator;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.models.mapData.MapValidator;
import client.models.mapData.enums.FortState;

public class MapController {
	
	private ClientMap myMap;
	private ClientMapGenerator myMapGenerator;
	private MapValidator validator;

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
	
	public MapController(ClientMap myMap, MapValidator validator, ClientMapGenerator mapGenerator) {
		super();
		this.myMap = myMap;
		this.validator = validator;
		this.myMapGenerator = mapGenerator;
	}


	public void generateMap() {

		myMapGenerator.createMap();
		
		ClientMap myNewMap = new ClientMap(myMapGenerator.getFields());	
		
		//placeFort(myNewMap);
		Coordinates fortPos = myMapGenerator.placeFort();
		for( Entry<Coordinates, MapField> mapEntry : myNewMap.getFields().entrySet() ) {
			if (mapEntry.getKey().equals(fortPos)) {
				mapEntry.getValue().setFortState(FortState.MYFORT);
			}
		}
		//myNewMap.getFields().get(myMapGenerator.placeFort()).setFortState(FortState.MYFORT);
		myNewMap.setxSize(8);
		myNewMap.setySize(4);
		
		if(validator.validateMap(myNewMap)) {
			
			this.myMap = myNewMap;
			return;
		
		} else {
		
			generateMap();

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
	

	public void placeFort(ClientMap myMap) {
		for( Entry<Coordinates, MapField> mapEntry : myMap.getFields().entrySet() ) {
			if (mapEntry.getKey().equals(myMapGenerator.placeFort())) {
				mapEntry.getValue().setFortState(FortState.MYFORT);
			}
		}
		
	}
	
	//------------------------- test print
	public MapField getMyFortField() {

		for( Map.Entry<Coordinates, MapField> mapEntry : myMap.getFields().entrySet() ) {
			if (mapEntry.getValue().getFortState() == FortState.MYFORT) {
				return mapEntry.getValue();
			}
		}
		return null;
	}
	//------------------------- test print

	

}
