package server.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.EMove;
import MessagesBase.MessagesFromClient.ETerrain;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.HalfMapNode;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromServer.EFortState;
import MessagesBase.MessagesFromServer.EPlayerGameState;
import MessagesBase.MessagesFromServer.EPlayerPositionState;
import MessagesBase.MessagesFromServer.ETreasureState;
import MessagesBase.MessagesFromServer.FullMap;
import MessagesBase.MessagesFromServer.FullMapNode;
import MessagesBase.MessagesFromServer.PlayerState;
import server.models.InternalHalfMap;
import server.models.MapNode;
import server.models.Player;
import server.enums.FortState;
import server.enums.MapFieldType;
import server.enums.MoveCommand;
import server.enums.PlayerPositionState;
import server.enums.ServerPlayerState;
import server.enums.TreasureState;
import server.models.Coordinates;
import server.models.GameData;
import server.models.InternalFullMap;

public class NetworkConverter {
	
	private static final Logger logger = LoggerFactory.getLogger(NetworkConverter.class);

	public InternalHalfMap convertHalfMapFrom(HalfMap halfMap) {
		
		InternalHalfMap toReturn = new InternalHalfMap();
		Map<Coordinates, MapNode> newMp = new HashMap<Coordinates, MapNode>();
		Set<HalfMapNode> halfMapFields = halfMap.getMapNodes().stream().collect(Collectors.toSet());

		for (HalfMapNode node : halfMapFields) {
			MapNode field = new MapNode();
			field = convertMapNodeFrom(node);
			if (node.isFortPresent()) {
				Coordinates fortPos = new Coordinates(node.getX(), node.getY());
				toReturn.setFortPos(fortPos);
			}
			Coordinates pos = new Coordinates(node.getX(), node.getY());
			newMp.put(pos, field);
		}
	
		toReturn.setFields(newMp);

		return toReturn;
		
	}
	
	// enemy flag is true if the player is an enemy
	public PlayerState convertPlayerTo(GameData game, Player player, boolean enemy) {
		
		PlayerState toReturn = new PlayerState();
		if (enemy) {
			String firstName = player.getPlayerReg().getStudentFirstName();
			String lastName = player.getPlayerReg().getStudentLastName();
			String uaccount = player.getPlayerReg().getStudentUAccount();
			EPlayerGameState state = EPlayerGameState.MustWait;
			if (game.getWinnerId() != null) {
				if (game.getWinnerId().equals(player.getPlayerId())) {
					state = EPlayerGameState.Won;	
				} else {
					state = EPlayerGameState.Lost;
				}		
			} else {
				if (game.myTurn(player.getPlayerId())) {
					state = EPlayerGameState.MustAct;
				} else {
					state = EPlayerGameState.MustWait;
				} 
			}
			
			
			UniquePlayerIdentifier identifier = new UniquePlayerIdentifier(UUID.randomUUID().toString());
			boolean collectedTreasure = player.isHasCollectedTreasure();
			return new PlayerState(firstName, lastName, uaccount, state, identifier, collectedTreasure);
		}
		String firstName = player.getPlayerReg().getStudentFirstName();
		String lastName = player.getPlayerReg().getStudentLastName();
		String uaccount = player.getPlayerReg().getStudentUAccount();
		EPlayerGameState state = EPlayerGameState.MustWait;
		if (game.getWinnerId() != null) {
			if (game.getWinnerId().equals(player.getPlayerId())) {
				state = EPlayerGameState.Won;	
			} else {
				state = EPlayerGameState.Lost;
			}		
		} else {
			if (game.myTurn(player.getPlayerId())) {
				state = EPlayerGameState.MustAct;
			} else {
				state = EPlayerGameState.MustWait;
			} 
		}
		
		UniquePlayerIdentifier identifier = new UniquePlayerIdentifier(player.getPlayerId());
		boolean collectedTreasure = player.isHasCollectedTreasure();
		return new PlayerState(firstName, lastName, uaccount, state, identifier, collectedTreasure);
	
	}
	
	private MapNode convertMapNodeFrom(HalfMapNode node) {
		
		MapNode toReturn = new MapNode();
		
		toReturn.setFieldType(convertTerrainTypeFrom(node.getTerrain()));
		
		//toReturn.setPlayerPositionState(convertPlayerPositionStateFrom(node.ge));
		
		Coordinates pos = new Coordinates(node.getX(), node.getY());
		toReturn.setPosition(pos);
		//toReturn.setTreasureState(convertTreasureStateFrom(node.getTreasureState()));
		
		
		return toReturn;
		
	}
	
	private ETerrain convertTerrainTypeTo(MapFieldType fieldType) {
		
		if(fieldType == MapFieldType.GRASS) {
			return ETerrain.Grass;
		}
		
		if(fieldType == MapFieldType.MOUNTAIN) {
			return ETerrain.Mountain;
		}
		
		if(fieldType == MapFieldType.WATER) {
			return ETerrain.Water;
		}
		return null;
	}
	
	private MapFieldType convertTerrainTypeFrom(ETerrain fieldType) {
		
		if(fieldType == ETerrain.Grass) {
			return MapFieldType.GRASS;
		}
		
		if(fieldType == ETerrain.Mountain) {
			return MapFieldType.MOUNTAIN;
		}
		
		if(fieldType == ETerrain.Water) {
			return MapFieldType.WATER;
		}
		return null;
	}
	
	private ServerPlayerState convertPlayerStateFrom(EPlayerGameState plState) {
		
		if(plState == EPlayerGameState.MustAct) {
			return ServerPlayerState.MUSTACT;
		}
		if(plState == EPlayerGameState.MustWait) {
			return ServerPlayerState.MUSTWAIT;		
		}
		if(plState == EPlayerGameState.Won) {
			return ServerPlayerState.WON;
		}
		if(plState == EPlayerGameState.Lost) {
			return ServerPlayerState.LOST;
		}
		return null;
		
	}
	
	private PlayerPositionState convertPlayerPositionStateFrom(EPlayerPositionState plPosState) {
		
		if(plPosState == EPlayerPositionState.NoPlayerPresent) {
			return PlayerPositionState.NOPLAYER;
		}
		if(plPosState == EPlayerPositionState.EnemyPlayerPosition) {
			return PlayerPositionState.ENEMYPLAYER;
		}
		if(plPosState == EPlayerPositionState.MyPlayerPosition) {
			return PlayerPositionState.MYPLAYER;
		}
		if(plPosState == EPlayerPositionState.BothPlayerPosition) {
			return PlayerPositionState.BOTH;
		}
		return null;
	}
	
	private FortState convertFortStateFrom(EFortState fortState) {
		
		if(fortState == EFortState.EnemyFortPresent) {
			return FortState.ENEMYFORT;
		}
		if(fortState == EFortState.MyFortPresent) {
			return FortState.MYFORT;
		}
		if(fortState == EFortState.NoOrUnknownFortState) {
			return FortState.UNKNOWNIFFORT;
		}
		
		return null;
		
	}
	
	private TreasureState convertTreasureStateFrom(ETreasureState treasureState) {
		
		if(treasureState == ETreasureState.MyTreasureIsPresent) {
			return TreasureState.MYTREASURE;
		}
		if(treasureState == ETreasureState.NoOrUnknownTreasureState) {
			return TreasureState.UNKNOWNIFTREASURE;
		}
		return null;
	}
	
	private FullMapNode convertMapNodeTo(MapNode node) {
		
		FullMapNode toReturn = new FullMapNode();
		
		ETerrain fieldType = convertTerrainTypeTo(node.getFieldType());
		//toReturn.setPlayerPositionState(convertPlayerPositionStateFrom(node.ge));
		
		Coordinates pos = node.getPosition();
		
		//toReturn.setTreasureState(convertTreasureStateFrom(node.getTreasureState()));
		
		
		return toReturn;
		
	}
	
	public Coordinates getRandomEnemyPos(InternalFullMap myMap) {
		Coordinates toRet = new Coordinates();
		Random randomNo = new Random();
		
		if (myMap.getxSize() == 8) {
			int randomFortX = randomNo.nextInt(8);
			toRet.setX(randomFortX);
			int randomFortY = randomNo.nextInt(8);
			toRet.setY(randomFortY);
			
			return toRet;
		}
		
		int randomFortX = randomNo.nextInt(16);
		toRet.setX(randomFortX);
		int randomFortY = randomNo.nextInt(4);
		toRet.setY(randomFortY);
		
		return toRet;
	}
	
	// server full map to network fullmap
	public Optional<FullMap> convertServerFullMapTo(UniquePlayerIdentifier playerID, GameData game) {
		int roundNo = game.getRoundNo();
		
		InternalFullMap myMap = game.getFullMap();
		Player myPlayer = game.getPlayerWithId(playerID.getUniquePlayerID());
		Player enemyPlayer = game.getTheOtherPlayer(playerID.getUniquePlayerID());
		
		FullMap toRet = new FullMap();
		Set<FullMapNode> mapNodes = new HashSet<FullMapNode>();

		Coordinates enemyPos = getRandomEnemyPos(myMap);
		Coordinates myFortPos = myPlayer.getHalfMap().getFortPos();
		Coordinates enemyFortPos = enemyPlayer.getHalfMap().getFortPos();
		Coordinates actualEnemyPosition = enemyPlayer.getCurrPos();
		
		logger.info("				 FIRST enemy pos rounds > 10 " + enemyPlayer.getCurrPos().getX() + " " + enemyPlayer.getCurrPos().getY());

		for( Map.Entry<Coordinates, MapNode> mapEntry : myMap.getFields().entrySet() ) {
			//FullMapNode toReturn = new FullMapNode();
			
			ETerrain fieldType = convertTerrainTypeTo(mapEntry.getValue().getFieldType());
			//toReturn.setPlayerPositionState(convertPlayerPositionStateFrom(node.ge));
			
			Coordinates pos = mapEntry.getKey();
			//de aici lucreaza
			ETreasureState treasure = ETreasureState.NoOrUnknownTreasureState;
			EFortState fort = EFortState.NoOrUnknownFortState;
			EPlayerPositionState playerPos = EPlayerPositionState.NoPlayerPresent;
			
			if (myFortPos.equals(mapEntry.getKey())) {
				fort = EFortState.MyFortPresent;
			}
			if (enemyFortPos.equals(mapEntry.getKey())) {
				if (myPlayer.isShowEnemyFort()) {
					fort = EFortState.EnemyFortPresent;	
				}
			}
			
			if (myPlayer.isHasCollectedTreasure()) {
				treasure = ETreasureState.NoOrUnknownTreasureState;
			} else if (mapEntry.getValue().getTreasureState() == TreasureState.MYTREASURE) {
				treasure = ETreasureState.MyTreasureIsPresent;
			}
			
			if (roundNo > 10) {
				
				if (myPlayer.getCurrPos().equals(mapEntry.getKey()) && enemyPlayer.getCurrPos().equals(mapEntry.getKey())) {
				
					playerPos = EPlayerPositionState.BothPlayerPosition;	
				} else if (myPlayer.getCurrPos().equals(mapEntry.getKey())) {
					playerPos = EPlayerPositionState.MyPlayerPosition;
					logger.info("my player pos rounds > 10 " + mapEntry.getKey().getX() + " " + mapEntry.getKey().getY());
				} else if (mapEntry.getKey().equals(actualEnemyPosition)) {
					playerPos = EPlayerPositionState.EnemyPlayerPosition;
					logger.info("enemy pos rounds > 10 " + mapEntry.getKey().getX() + " " + mapEntry.getKey().getY());
				}
			
			} else {
				
				if (myPlayer.getCurrPos().equals(mapEntry.getKey()) && enemyPos.equals(mapEntry.getKey())) {
					playerPos = EPlayerPositionState.BothPlayerPosition;
				} else if (myPlayer.getCurrPos().equals(mapEntry.getKey())) {
					playerPos = EPlayerPositionState.MyPlayerPosition;
					logger.info("my player pos rounds < 10 " + mapEntry.getKey().getX() + " " + mapEntry.getKey().getY());
				} else if (enemyPos.equals(mapEntry.getKey())) {
					playerPos = EPlayerPositionState.EnemyPlayerPosition;	
					logger.info("enemy pos rounds < 10 " + mapEntry.getKey().getX() + " " + mapEntry.getKey().getY());
				}
			}

			FullMapNode fullMapNode = new FullMapNode(fieldType, playerPos, treasure, fort, pos.getX(), pos.getY());
			mapNodes.add(fullMapNode);
			
			
		}
	
		toRet = new FullMap(mapNodes);
		return Optional.of(toRet);
		
	}
	
	public Optional<FullMap> convertIHalfMapToNetworkFullMap(Player player, GameData gameState) {

		FullMap toRet = new FullMap();
		Set<FullMapNode> mapNodes = new HashSet<FullMapNode>();
		
		
		InternalHalfMap myMap = player.getHalfMap();

		Coordinates myFortPos = myMap.getFortPos();
		

		for( Map.Entry<Coordinates, MapNode> mapEntry : myMap.getFields().entrySet() ) {
			
			ETerrain fieldType = convertTerrainTypeTo(mapEntry.getValue().getFieldType());
			
			Coordinates pos = mapEntry.getKey();
			
			ETreasureState treasure = ETreasureState.NoOrUnknownTreasureState;
			EFortState fort = EFortState.NoOrUnknownFortState;
			EPlayerPositionState playerPos = EPlayerPositionState.NoPlayerPresent;
			
			if (myFortPos.equals(mapEntry.getKey())) {
				fort = EFortState.MyFortPresent;
			}
		
			if (player.getCurrPos().equals(mapEntry.getKey())) {
				playerPos = EPlayerPositionState.MyPlayerPosition;
			} 
		
			FullMapNode fullMapNode = new FullMapNode(fieldType, playerPos, treasure, fort, pos.getX(), pos.getY());
			mapNodes.add(fullMapNode);
			
		}
	
		toRet = new FullMap(mapNodes);
		return Optional.of(toRet);
		
	}
	
	public MoveCommand convertMoveFrom(PlayerMove move) {
		if (move.getMove() == EMove.Up) {
			return MoveCommand.UP;
		}
		
		if (move.getMove() == EMove.Down) {
			return MoveCommand.DOWN;
		}
		
		if (move.getMove() == EMove.Left) {
			return MoveCommand.LEFT;
		}
		
		if (move.getMove() == EMove.Right) {
			return MoveCommand.RIGHT;
		}
		return null;
	}
	
	public PlayerMove convertMoveTo(String uniquePlayerID, MoveCommand move) {
		if (move == MoveCommand.UP) {
			return PlayerMove.of(uniquePlayerID, EMove.Up);
		}
		
		if (move == MoveCommand.DOWN) {
			return PlayerMove.of(uniquePlayerID, EMove.Down);
		}
		
		if (move == MoveCommand.LEFT) {
			return PlayerMove.of(uniquePlayerID, EMove.Left);
		}
		
		if (move == MoveCommand.RIGHT) {
			return PlayerMove.of(uniquePlayerID, EMove.Right);
		}
		return null;
	}
	
	
}
