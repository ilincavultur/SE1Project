package client.movement;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.mockito.Mockito;

import client.controllers.MovementController;
import client.models.gameData.GameStateData;
import client.models.mapData.ClientMap;
import client.models.mapData.ClientMapGenerator;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.models.mapData.enums.MapFieldType;
import client.movement.enums.MoveCommand;

public class TargetTests {
	
	@Test
	public void CurrentPosition_CalculateNeighbours_ExpectedCorrectNeighbours() {
	
		//arrange
		Random randomNo = new Random();
		int randomX = randomNo.nextInt(8);
		int randomY = randomNo.nextInt(4);

		ClientMapGenerator mapGenerator = new ClientMapGenerator();
		ClientMap myMap = new ClientMap();
		mapGenerator.createMap();
		
		Coordinates up = new Coordinates();
		Coordinates right = new Coordinates();
		Coordinates down = new Coordinates();
		Coordinates left = new Coordinates();
		Coordinates upNeighbour = new Coordinates();
		Coordinates rightNeighbour = new Coordinates();
		Coordinates downNeighbour = new Coordinates();
		Coordinates leftNeighbour = new Coordinates();
	
		myMap.setFields(mapGenerator.getFields());
		myMap.setxSize(8);
		myMap.setySize(4);
	
		Coordinates coords = new Coordinates(randomX, randomY);
		
		//act
		
		if (coords.getUpNeighbour(myMap) != null) {
			
			upNeighbour = coords.getUpNeighbour(myMap);
			up = new Coordinates(randomX,randomY-1);
		}
		
		if (coords.getRightNeighbour(myMap) != null) {
			
			rightNeighbour = coords.getRightNeighbour(myMap);
			right = new Coordinates(randomX+1,randomY);
		}
		
		if (coords.getDownNeighbour(myMap) != null) {
			
			downNeighbour = coords.getDownNeighbour(myMap);
			down = new Coordinates(randomX,randomY+1);
		}
		
		if (coords.getLeftNeighbour(myMap) != null) {
			
			leftNeighbour = coords.getLeftNeighbour(myMap);
			left = new Coordinates(randomX-1,randomY);
		}
								
		boolean result = ((upNeighbour.equals(up)) && (rightNeighbour.equals(right)) && (downNeighbour.equals(down)) && (leftNeighbour.equals(left)));
		
		//assert
		assertEquals(true, result);
	}
	
	@Test
	public void AvailableUnvisitedNeighbours_CalculateNextAvailableNeighbour_ExpectedOneOfTheNeighbours() {
		
		//arrange
		ClientMapGenerator mapGenerator = new ClientMapGenerator();
		ClientMap myMap = new ClientMap();
		mapGenerator.createMap();
		Coordinates myPos = new Coordinates(2,2);
		Coordinates neighbour1 = new Coordinates(2,1);
		Coordinates neighbour2 = new Coordinates(3,2);
		Coordinates neighbour3 = new Coordinates(2,3);
		Coordinates neighbour4 = new Coordinates(1,2);

		mapGenerator.getFields().get(neighbour3).setType(MapFieldType.GRASS);
		mapGenerator.getFields().get(neighbour1).setType(MapFieldType.WATER);
		mapGenerator.getFields().get(neighbour2).setType(MapFieldType.WATER);
		mapGenerator.getFields().get(neighbour4).setType(MapFieldType.MOUNTAIN);
		
		myMap.setFields(mapGenerator.getFields());
		myMap.setxSize(8);
		myMap.setySize(4);
		GameStateData gsd = Mockito.mock(GameStateData.class);
		Coordinates coords = new Coordinates(2,2);
	
		gsd.setPlayerPosition(myPos);
		TargetSelector targetSelector = new TargetSelector(gsd);
		targetSelector.setMyMap(myMap);
		targetSelector.setGameState(gsd);
		List<Coordinates> unvisitedTotal = new ArrayList<Coordinates>();
		for( Map.Entry<Coordinates, MapField> mapEntry : myMap.getFields().entrySet() ) {
			if (mapEntry.getValue().getType() != MapFieldType.WATER) {
				unvisitedTotal.add(mapEntry.getKey());
			}
			
		}
		targetSelector.setUnvisitedTotal(unvisitedTotal);
		
		Mockito.when(coords.getFieldsAround(myMap)).then((value) -> {
			
			Map<String, Coordinates> neighbours = new HashMap<String, Coordinates>();
			Coordinates up = coords.getUpNeighbour(myMap);
			Coordinates right = coords.getRightNeighbour(myMap);
			Coordinates down = coords.getDownNeighbour(myMap);
			Coordinates left = coords.getLeftNeighbour(myMap);
			if (up != null && myMap.getFields().get(up).getType() != MapFieldType.WATER) {
				neighbours.put("up", up);
				System.out.println(up.getX() + up.getY());
			}
			if (right != null && myMap.getFields().get(right).getType() != MapFieldType.WATER) {
				neighbours.put("right", right);	
				System.out.println(right.getX() + right.getY());
			}
			if (down != null && myMap.getFields().get(down).getType() != MapFieldType.WATER) {
				neighbours.put("down", down);
				System.out.println(down.getX() + down.getY());
			}
			if (left != null && myMap.getFields().get(left).getType() != MapFieldType.WATER) {
				neighbours.put("left", left);
				System.out.println(left.getX() + left.getY());
			}
			
			return neighbours;
			
		});
		
		//act
		Coordinates nextTarget = targetSelector.nextAvailableNeighbour(coords, myMap.getFields());
		boolean result = (nextTarget.equals(neighbour3) || nextTarget.equals(neighbour4));
		
		//assert
		assertEquals(true, result);
	}
	
	
	@Test
	public void AvailableListOfMoves_GetNextMove_ExpectedFirstElementOfList() {
		
		//arrange
		List<MoveCommand> movesList = new ArrayList<MoveCommand>();
		movesList.add(MoveCommand.LEFT);
		movesList.add(MoveCommand.DOWN);
		movesList.add(MoveCommand.LEFT);
		movesList.add(MoveCommand.LEFT);
		movesList.add(MoveCommand.UP);
		movesList.add(MoveCommand.UP);
		List<MoveCommand> movesListCopy = new ArrayList<MoveCommand>(movesList);
		
		PathCalculator pathCalc = Mockito.mock(PathCalculator.class);
		
		MovementController moveController = new MovementController(movesList, pathCalc);
		Mockito.when(pathCalc.getNextMove(movesList)).then((value) -> {
			return movesList.get(0);
		});
		
		//act
		MoveCommand nextMove = moveController.getNextMove();
		boolean result = (movesListCopy.get(0) == nextMove);
		
		
		//assert
		assertEquals(true, result);
	}

}
