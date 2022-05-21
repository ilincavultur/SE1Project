package client.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.models.mapData.enums.FortState;
import client.models.mapData.enums.MapFieldType;
import client.models.mapData.enums.PlayerPositionState;
import client.models.mapData.enums.TreasureState;
import client.exceptions.MapException;

public class CLI implements PropertyChangeListener {
	
	private void printMapField(MapField myMapField) {
	
		System.out.print(myMapField.getPosition().getX());
		System.out.print(myMapField.getPosition().getY());
		
		if (myMapField.getTreasureState() == TreasureState.MYTREASURE) {
			System.out.print("!!!T!!!");
		}
		
		if (myMapField.getFortState() == FortState.ENEMYFORT) {
			System.out.print("!!!EF!!!");
		}
		
		if(myMapField.getPlayerPositionState() == PlayerPositionState.BOTH || myMapField.getPlayerPositionState() == PlayerPositionState.MYPLAYER) {
			System.out.print("|A|");
		}
		
		if(myMapField.getPlayerPositionState() == PlayerPositionState.BOTH || myMapField.getPlayerPositionState() == PlayerPositionState.ENEMYPLAYER) {
			System.out.print("E");
		}
		
		if(myMapField.getType() == MapFieldType.GRASS) {
			System.out.print("G    ");
		}
		if(myMapField.getType() == MapFieldType.MOUNTAIN) {
			System.out.print("M    ");			
		}
		if(myMapField.getType() == MapFieldType.WATER) {
			System.out.print("W    ");
		}
		
	}
 	
	public void printMap(ClientMap myMap) {
		

		for (int y = 0; y < myMap.getySize(); y++) {
		
			for(int x =0; x < myMap.getxSize(); x++) {
				Coordinates pos = new Coordinates(x, y);
				printMapField(myMap.getFields().get(pos));

				/*if (myMap.getxSize() != 8) {
					
					if (x == 7) {
						System.out.print("||    ");
						
					}
				
				}*/
				
			}
			/*if (myMap.getxSize() == 8) {
				
				if (y == 3) {
					System.out.println("\n");
					System.out.println("------------------------------------------------------");
					System.out.println("\n");	
				} else {
					System.out.println("\n");	
				}
				
			} else {*/
				System.out.println("\n");
			//}
			
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		ClientMap map = (ClientMap)evt.getNewValue();
		printMap(map);
		
	}
	
	
}
