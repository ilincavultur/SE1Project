package client.ui;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import client.models.gameData.GameStateData;
import client.models.mapData.ClientMap;
import client.models.mapData.ClientMapGenerator;
import client.models.mapData.Coordinates;
import client.models.mapData.enums.FortState;
import client.models.mapData.enums.PlayerPositionState;
import client.models.mapData.enums.TreasureState;

public class UiTest {
	// CODE TAKEN FROM https://stackoverflow.com/questions/8708342/redirect-console-output-to-string-in-java
	
	@Test
	public void FullMapPresent_GameStateDataChanges_ExpectedFullMapPrintedWithNewPosition() {
		
		//arrange
		ClientMapGenerator mapGenerator = new ClientMapGenerator();
		ClientMapGenerator secondMapGenerator = new ClientMapGenerator();
		ClientMap myFirstMap = new ClientMap();
		ClientMap mySecondMap = new ClientMap();
		GameStateData gsdFirst = new GameStateData();
		GameStateData gsdSecond = new GameStateData();
		CLI ui = new CLI();
		gsdFirst.registerInterestedView(ui);
		gsdSecond.registerInterestedView(ui);
		mapGenerator.createMap();
		secondMapGenerator.createMap();
		Coordinates myPos = new Coordinates(0,0);
		Coordinates fortPos = new Coordinates(1,0);
		Coordinates treasurePos = new Coordinates(5,0);
		mapGenerator.getFields().get(myPos).setPlayerPositionState(PlayerPositionState.MYPLAYER);
		mapGenerator.getFields().get(fortPos).setFortState(FortState.MYFORT);
		mapGenerator.getFields().get(treasurePos).setTreasureState(TreasureState.MYTREASURE);
		Coordinates mySecondPos = new Coordinates(0,1);
		secondMapGenerator.getFields().get(mySecondPos).setPlayerPositionState(PlayerPositionState.MYPLAYER);
		secondMapGenerator.getFields().get(fortPos).setFortState(FortState.MYFORT);
		secondMapGenerator.getFields().get(treasurePos).setTreasureState(TreasureState.MYTREASURE);
		myFirstMap.setFields(mapGenerator.getFields());
		myFirstMap.setxSize(8);
		myFirstMap.setySize(4);
		mySecondMap.setFields(secondMapGenerator.getFields());
		mySecondMap.setxSize(8);
		mySecondMap.setySize(4);
		
		// CODE TAKEN FROM START https://stackoverflow.com/questions/8708342/redirect-console-output-to-string-in-java
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream consoleOutput = new PrintStream(outputStream);
		PrintStream old = System.out;
		System.setOut(consoleOutput);
		
		// CODE TAKEN FROM END https://stackoverflow.com/questions/8708342/redirect-console-output-to-string-in-java
		
		// info for the test user
		System.out.println("Before changed position:");
		
		// act
		gsdFirst.setPlayerPosition(myPos);
		gsdFirst.setFullMap(myFirstMap);
		
		// info for the test user
		System.out.println("After changed position:");
		gsdSecond.setPlayerPosition(mySecondPos);
		gsdSecond.setFullMap(mySecondMap);
		boolean result1 = outputStream.toString().contains("00|A|");
		boolean result2 = outputStream.toString().contains("01|A|");
	
		// CODE TAKEN FROM START https://stackoverflow.com/questions/8708342/redirect-console-output-to-string-in-java
		System.out.flush();
		System.setOut(old);
		System.out.println(outputStream.toString());
		// CODE TAKEN FROM END https://stackoverflow.com/questions/8708342/redirect-console-output-to-string-in-java
		
		//assert
		assertEquals(true, result1);
		assertEquals(true, result2);
	}
}
