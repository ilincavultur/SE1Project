package client.map;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;

import client.controllers.MapController;
import client.models.mapData.ClientMap;
import client.models.mapData.ClientMapGenerator;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.models.mapData.MapValidator;
import client.models.mapData.enums.MapFieldType;

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
	
	@Test
	public void GenerateClientMap_SetFieldTypes_ExpectedVerifyFieldTypesNoTrue() {
		
		//arrange
		ClientMapGenerator mapGenerator = new ClientMapGenerator();
		MapValidator mapValidator = new MapValidator();
		ClientMap myMap = new ClientMap();
		MapController mapController = new MapController(myMap, mapValidator, mapGenerator);
		
		//act
		mapGenerator.createMap();
		myMap.setFields(mapGenerator.getFields());
		mapController.setMyMap(myMap);
		boolean result = mapValidator.verifyFieldTypesNo(myMap);
		
		//assert
		assertEquals(true, result);
	}
	
	@Test
	public void GenerateClientMap_SetFieldTypes_ExpectedHasNoIslandsTrue() {
		
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
		boolean result = mapValidator.hasNoIsland(myMap);
		
		//assert
		assertEquals(true, result);
	}
	
	@Test
	public void GenerateClientMap_ExceedWaterNoOnLongSide_ExpectedVerifyLongSidesFalse() {
		
		//arrange
		ClientMapGenerator mapGenerator = new ClientMapGenerator();
		MapValidator mapValidator = new MapValidator();
		ClientMap myMap = new ClientMap();
		MapController mapController = new MapController(myMap, mapValidator, mapGenerator);
		
		//act
		mapGenerator.createMap();
		myMap.setFields(mapGenerator.getFields());
		myMap.setxSize(8);
		myMap.setySize(4);
		mapController.setMyMap(myMap);
	
		for( Map.Entry<Coordinates, MapField> mapEntry : myMap.getFields().entrySet() ) {
			if (mapEntry.getKey().getY() == 0 || mapEntry.getKey().getY() == 7) {
				if (mapEntry.getValue().getType() == MapFieldType.GRASS || mapEntry.getValue().getType() == MapFieldType.MOUNTAIN) {
					mapEntry.getValue().setType(MapFieldType.WATER);
				}
			}
		}
		boolean result = mapValidator.verifyLongSides(myMap);
		
		//assert
		assertEquals(false, result);
	}
	
	@Test
	public void GenerateClientMap_ExceedWaterNoOnShortSide_ExpectedVerifyShortSidesFalse() {
		
		//arrange
		ClientMapGenerator mapGenerator = new ClientMapGenerator();
		MapValidator mapValidator = new MapValidator();
		ClientMap myMap = new ClientMap();
		MapController mapController = new MapController(myMap, mapValidator, mapGenerator);
		
		//act
		mapGenerator.createMap();
		myMap.setFields(mapGenerator.getFields());
		myMap.setxSize(8);
		myMap.setySize(4);
		mapController.setMyMap(myMap);
	
		for( Map.Entry<Coordinates, MapField> mapEntry : myMap.getFields().entrySet() ) {
			if (mapEntry.getKey().getX() == 0 || mapEntry.getKey().getX() == 3) {
				if (mapEntry.getValue().getType() == MapFieldType.GRASS || mapEntry.getValue().getType() == MapFieldType.MOUNTAIN) {
					mapEntry.getValue().setType(MapFieldType.WATER);
				}
			}
		}
		boolean result = mapValidator.verifyShortSides(myMap);
		
		//assert
		assertEquals(false, result);
	}
}
