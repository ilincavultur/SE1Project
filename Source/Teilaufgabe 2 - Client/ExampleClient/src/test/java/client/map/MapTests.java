package client.map;

import org.junit.Test;
import static org.junit.Assert.*;

import client.controllers.MapController;
import client.models.mapData.ClientMap;
import client.models.mapData.ClientMapGenerator;
import client.models.mapData.MapValidator;

public class MapTests {

	@Test
	public void GenerateClientMap_PlaceFort_ExpectedHasFortTrue() {
		
		//arrange
		ClientMapGenerator mapGenerator = new ClientMapGenerator();
		MapValidator mapValidator = new MapValidator();
		ClientMap myMap = new ClientMap();
		MapController mapController = new MapController(myMap, mapValidator, mapGenerator);
		
		//act
		mapGenerator.createMap();
		//mapGenerator.placeFort();
		myMap.setFields(mapGenerator.getFields());
		mapController.setMyMap(myMap);
		mapController.placeFort(myMap);
		boolean result = mapValidator.hasFort(myMap);
		
		//assert
		assertEquals(true, result);
	}
}
