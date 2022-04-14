package client.ui;

import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.models.mapData.enums.MapFieldType;

public class CLI {
	public void printMapField(MapField myMapField) {
		System.out.print(myMapField.getPosition().getX());
		System.out.print(myMapField.getPosition().getY());
		if(myMapField.getType() == MapFieldType.GRASS) {
			System.out.print("G ");
		}
		if(myMapField.getType() == MapFieldType.MOUNTAIN) {
			System.out.print("M ");			
		}
		if(myMapField.getType() == MapFieldType.WATER) {
			System.out.print("W ");
		}
		
	}
 	
	public void printMap(ClientMap myMap) {

		for (int y = 0; y < 4; y++) {
		
			for(int x =0; x < 8; x++) {
				Coordinates pos = new Coordinates(x, y);
				printMapField(myMap.getFields().get(pos));
			}
			System.out.println("\n");
		}
	}
}
