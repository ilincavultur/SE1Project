package client.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import client.models.gameData.GameStateData;
import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.models.mapData.enums.MapFieldType;
import client.models.mapData.enums.PlayerPositionState;

public class CLI implements PropertyChangeListener {
	public void printMapField(MapField myMapField) {
		System.out.print(myMapField.getPosition().getX());
		System.out.print(myMapField.getPosition().getY());
		if(myMapField.getPlayerPositionState() == PlayerPositionState.BOTH || myMapField.getPlayerPositionState() == PlayerPositionState.MYPLAYER) {
			System.out.print("A ");
		}
		//System.out.print(myMapField.getMoves());
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
		
		System.out.println("ysize" + myMap.getySize());
		System.out.println("xsize" + myMap.getxSize());

		for (int y = 0; y < myMap.getySize(); y++) {
		
			for(int x =0; x < myMap.getxSize(); x++) {
				Coordinates pos = new Coordinates(x, y);
				printMapField(myMap.getFields().get(pos));
			}
			System.out.println("\n");
		}
	}
	
	public void View(GameStateData gameStateData) {
		gameStateData.registerInterestedView(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		// ClientMap map = (ClientMap)evt.getNewValue();
		
	}
	
	
}
