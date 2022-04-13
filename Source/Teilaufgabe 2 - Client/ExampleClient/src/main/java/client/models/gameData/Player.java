package client.models.gameData;

import client.models.gameData.enums.PlayerState;
import client.models.mapData.Coordinates;

public class Player {
	String playerID;
	PlayerState playerState;
	Boolean hasCollectedTreasure;
	Coordinates playerPosition;
}
