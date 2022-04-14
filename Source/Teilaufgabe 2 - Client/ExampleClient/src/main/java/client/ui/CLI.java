package client.ui;

import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.models.mapData.enums.MapFieldType;

public class CLI {
	public void printMapField(MapField myMapField) {
		System.out.println("Coordinates: " + myMapField.getPosition().getX() + " " + myMapField.getPosition().getY());
		if(myMapField.getType() == MapFieldType.GRASS) {
			System.out.println("GRASS");
		}
		if(myMapField.getType() == MapFieldType.MOUNTAIN) {
			System.out.println("MOUNTAIN");			
		}
		if(myMapField.getType() == MapFieldType.WATER) {
			System.out.println("WATER");
		}
		
	}
 	
	public void printMap(ClientMap myMap) {
		System.out.println(myMap.getFields() + "e ok");

		for (int i = 0 ; i < 7; i++) {
			for(int j = 0; j < 3; j++) {
				Coordinates pos = new Coordinates(i, j);
				printMapField(myMap.getFields().get(pos));
			}
		}
	}
}
