package server.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import server.controllers.GameStateController;
import server.exceptions.HalfMapException;
import server.exceptions.NotEnoughPlayersException;
import server.validation.BothPlayersRegisteredRule;
import server.models.Coordinates;
import server.models.InternalHalfMap;
import server.models.GameData;
import server.network.NetworkConverter;
import server.validation.FortRule;
import server.validation.HalfMapSizeRule;
import server.validation.TerrainsNumberRule;

public class MapTests {
	FortRule fortRule = new FortRule();
	HalfMapSizeRule sizeRule = new HalfMapSizeRule();
	TerrainsNumberRule terrainsRule = new TerrainsNumberRule();
	BothPlayersRegisteredRule bothPlayersRegisteredRule = new BothPlayersRegisteredRule();
	GameStateController controller = new GameStateController();
	NetworkConverter converter = new NetworkConverter();
	UniquePlayerIdentifier playerId = new UniquePlayerIdentifier(controller.createUniquePlayerId());
	UniqueGameIdentifier gameId = controller.createUniqueGameId();
	
	
	@Test
	public void ReceiveClientHalfMap_HasNoFort_ExpectedHasOneFortThrows() {
		
		//arrange
		HalfMap halfMap = createMap(false, false);
		
		//act
		HalfMapException e = Assertions.assertThrows(HalfMapException.class, () -> {
			fortRule.hasOneFort(halfMap);
		});
		
		//assert
		Assertions.assertEquals("One Fort needed, but found: 0", e.getMessage());
		
	}
	
	@Test
	public void ReceiveClientHalfMap_HasFortOnMountain_ExpectedIsFortOnGrassThrows() {
		
		//arrange
		HalfMap halfMap = createMap(false, true);
		
		//act
		HalfMapException e = Assertions.assertThrows(HalfMapException.class, () -> {
			fortRule.isFortOnGrass(getFortNode(halfMap));
		});
		
		//assert
		Assertions.assertEquals("Grass wanted, but found: Mountain", e.getMessage());
		
	}
	
	@Test
	public void ReceiveClientHalfMap_HasTooManyNodes_ExpectedHalfMapSizeRuleThrows() {
		
		//arrange
		HalfMap halfMap = createMap(true, true);
		
		//act
		HalfMapException e = Assertions.assertThrows(HalfMapException.class, () -> {
			sizeRule.validateHalfMap(halfMap);
		});
		
		//assert
		Assertions.assertEquals("32 Fields needed, but found33", e.getMessage());
		
	}
	
	@Test
	public void ReceiveClientHalfMap_HasWrongTerrainNo_ExpectedTerrainsNumberRuleThrows() {
		
		//arrange
		HalfMap halfMap = createMap(false, true);
		
		//act
		HalfMapException e = Assertions.assertThrows(HalfMapException.class, () -> {
			terrainsRule.validateHalfMap(halfMap);
		});
		
		//assert
		Assertions.assertEquals("At least 3 Fields needed, but found: 1", e.getMessage());
		
	}
	
	@Test
	public void ReceiveClientHalfMap_OnlyOnePlayerRegistered_ExpectedBothPlayersRegisteredRuleThrows() {
		
		//arrange
		HalfMap halfMap = createMap(false, true);
		InternalHalfMap hMap = converter.convertHalfMapFrom(halfMap);
		Map<String, GameData> games = new HashMap<String, GameData>();
		GameData gameData = new GameData();
		games.put(gameId.getUniqueGameID(), gameData);
		
		//act
		NotEnoughPlayersException e = Assertions.assertThrows(NotEnoughPlayersException.class, () -> {
			//terrainsRule.validateHalfMap(halfMap);
			bothPlayersRegisteredRule.validateGameState(games, playerId, gameId);
			
		});
		
		//assert
		Assertions.assertEquals("Client tried to send half Map but not both players were registered", e.getMessage());
		
	}
	
	public HalfMapNode createNode(int x, int y, boolean fortPresent, ETerrain terrain, Coordinates fortPos, boolean hasFort) {
		
		if (x == fortPos.getX() && y == fortPos.getY()) {
			if (hasFort) {
				fortPresent = true;
				terrain = ETerrain.Mountain;
			}
		}
		
		return new HalfMapNode(x, y, fortPresent, terrain);
	}
	
	public HalfMapNode getFortNode(HalfMap halfMap) {
		HalfMapNode toRet = new HalfMapNode();
		
		for (HalfMapNode node: halfMap.getMapNodes()) {
			if (node.isFortPresent()) {
				return node;
			}
		}
		
		return toRet;
	}
	
	public HalfMap createMap(boolean wrongSize, boolean hasFort) {
		
		Set<HalfMapNode> nodes = new HashSet<>();
		
		Coordinates fortPos = placeFort();
		
		for (int x = 0 ; x < 8; ++x) {
			for(int y=0; y<4; ++y) {
				
				nodes.add(createNode(x, y, false, ETerrain.Grass, fortPos, hasFort));		
				
			}
		}
		
		if (wrongSize) {
			nodes.add(createNode(17, 30, false, ETerrain.Grass, fortPos, hasFort));
		}
	
		return new HalfMap(playerId, nodes);
		
	}
	
	public Coordinates placeFort() {
		Random randomNo = new Random();
		
		int randomFortX = randomNo.nextInt(8);
		int randomFortY = randomNo.nextInt(4);
		
		Coordinates fortPos = new Coordinates(randomFortX, randomFortY);
		
		return fortPos;
		
	}

}
