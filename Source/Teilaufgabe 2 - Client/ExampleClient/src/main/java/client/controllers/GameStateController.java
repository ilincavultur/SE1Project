package client.controllers;

import MessagesBase.MessagesFromClient.PlayerRegistration;
import client.models.gameData.GameStateData;
import client.models.gameData.enums.ClientPlayerState;
import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.ui.CLI;

public class GameStateController {
	
	
	
	NetworkController networkController;
	MapController mapController;
	MovementController moveController;
	PlayerController playerController;
	CLI ui;
	

	public GameStateController(String gameId, String serverBaseUrl) {
		super();
		this.networkController = new NetworkController(gameId, serverBaseUrl);
		this.mapController = new MapController();
		this.moveController = new MovementController();
		this.playerController = new PlayerController();
		this.ui = new CLI();
	}
	
	public void startGame() {
		
		PlayerRegistration playerReg = new PlayerRegistration("Ilinca", "Vultur",
				"ilincav00");
	
		// register Player - network
		networkController.registerPlayer(playerReg);
		networkController.registerPlayer(playerReg);
		
		// generate halfmap - map
		mapController.generateMap();
		
		// print halfmap
		
		
		GameStateData state = new GameStateData(networkController.getGameState( networkController.getGameId(), networkController.getPlayerId()));
		// send halfmap - network
		while(state.getPlayerState() != ClientPlayerState.MUSTACT) {
			state = new GameStateData(networkController.getGameState(networkController.getGameId(), networkController.getPlayerId()));
		}
		networkController.sendMap(mapController.getMyMap());
		ui.printMap(mapController.getMyMap());
		//networkController.sendMap(mapController.getMyMap());
		//ui.printMap(mapController.getMyMap());
		
		
	}
	
	
	
	
	
}
