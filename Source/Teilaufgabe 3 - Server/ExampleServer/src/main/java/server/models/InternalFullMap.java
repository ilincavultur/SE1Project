package server.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.slf4j.LoggerFactory;

import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromServer.FullMapNode;
import ch.qos.logback.classic.Logger;
import server.enums.MapFieldType;
import server.enums.TreasureState;
import server.network.NetworkConverter;

public class InternalFullMap {
	
	private Map<Coordinates, MapNode> fields = new HashMap<Coordinates, MapNode>();
	private int xSize;
	private int ySize;
	private String firstMap;
	
	private static final Logger logger = (Logger) LoggerFactory.getLogger(InternalFullMap.class);

	public InternalFullMap() {
		super();
		setupFullMap();
	}

	public Map<Coordinates, MapNode> getFields() {
		return fields;
	}

	public void setFields(Map<Coordinates, MapNode> fields) {
		this.fields = fields;
	}

	/*
	 *  Choose random dimension of the map
	 */
	public void pickDimensions() {
		
		Random randomNo = new Random();
		int no = randomNo.nextInt(2);
		if (no == 0) {
			this.xSize = 8;
			this.ySize = 8;
		} else {
			this.xSize = 16;
			this.ySize = 4;
		}
		
	}
	
	/*
	 *  Choose which will be the first half (from left to right / from up to down)
	 */
	public String pickFirstHalf() {
		
		Random randomNo = new Random();
		int no = randomNo.nextInt(2);
		if (no == 0) {
			return "first";
		}
		return "second";
		
	}

	/*
	 *  Choose random dimension of the map
	 *  Choose which will be the first half (from left to right / from up to down)
	 */
	public void setupFullMap() {
		
		pickDimensions();
		
		this.firstMap = pickFirstHalf();
	
	}
	
	private void setFields (GameData game, Player player1, Player player2, Optional<InternalHalfMap> halfMap1, Optional<InternalHalfMap> halfMap2) {
		fields.putAll(halfMap1.get().getFields());
		
		player1.setFortPos(halfMap1.get().getFortPos());
		player1.setTreasurePos(halfMap1.get().getTreasurePos());
	
		transformCoordinates(player2, halfMap2.get());
		
		if (game.isChanged() == false) {
			game.setChanged(true);
			
			player1.setCurrPos(halfMap1.get().getFortPos());
			player2.setCurrPos(halfMap2.get().getFortPos());
		}
	}
	
	public void assembleFullMap(GameData game, List<Player> players, Optional<InternalHalfMap> halfMap1, Optional<InternalHalfMap> halfMap2) {
		
		if (this.firstMap.equals("first")) {
			setFields(game, players.get(0), players.get(1), halfMap1, halfMap2);
		} else {
			setFields(game, players.get(1), players.get(0), halfMap2, halfMap1);
		}
		
	}
	
	public int getxSize() {
		return xSize;
	}


	public void setxSize(int xSize) {
		this.xSize = xSize;
	}


	public int getySize() {
		return ySize;
	}


	public void setySize(int ySize) {
		this.ySize = ySize;
	}

	/*
	 *  Transform coordinates of the "second to come" map
	 */
	public void transformCoordinates(Player player, InternalHalfMap halfMap) {
		
		Coordinates oldFortPos = halfMap.getFortPos();
		Coordinates oldTreasurePos = halfMap.getTreasurePos();
		
		for( var eachNode : halfMap.getFields().entrySet() ) {
			
			Coordinates newPos = eachNode.getKey();
			
			// square map
			if (this.xSize == 8) {
				newPos = new Coordinates(eachNode.getKey().getX(), eachNode.getKey().getY() + 4);
			} else if (this.xSize == 16) {
				newPos = new Coordinates(eachNode.getKey().getX() + 8, eachNode.getKey().getY());
			}
			
			if (eachNode.getKey().equals(oldTreasurePos)) {
				player.setTreasurePos(newPos);
			}
			
			eachNode.getValue().setPosition(newPos);
			
			if (eachNode.getKey().equals(oldFortPos)) {
				halfMap.setFortPos(newPos);
				player.setFortPos(newPos);
			}
				
			fields.put(newPos, eachNode.getValue());
		}
		
	}
	
}
