package client.controllers;

import MessagesBase.UniquePlayerIdentifier;
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
		String pl1 = networkController.network.getPlayerID();
		System.out.println(pl1);
		networkController.registerPlayer(playerReg);
		String pl2 = networkController.network.getPlayerID();
		System.out.println(pl2);
		
		mapController.generateMap();
		
		GameStateData state = new GameStateData(networkController.getGameState( networkController.getGameId(), pl1));
		// send halfmap - network
		if(state.getPlayerState() == ClientPlayerState.MUSTACT) {
			networkController.sendMap(mapController.getMyMap(),pl1);
		} else {
			networkController.sendMap(mapController.getMyMap(),pl2);
			state = new GameStateData(networkController.getGameState( networkController.getGameId(), pl1));
			mapController.generateMap();
			if (state.getPlayerState() == ClientPlayerState.MUSTACT) {
				networkController.sendMap(mapController.getMyMap(),pl1);
			}
		}
		
		
		
		
		
		// generate halfmap - map
		//mapController.generateMap();
		
		
		
		
		/*GameStateData state = new GameStateData(networkController.getGameState( networkController.getGameId(), networkController.getPlayerId()));
		// send halfmap - network
		while(state.getPlayerState() != ClientPlayerState.MUSTACT) {
			state = new GameStateData(networkController.getGameState(networkController.getGameId(), networkController.getPlayerId()));
		}*/
		
		
		// corect 
		/*
		GameStateData state = new GameStateData(networkController.getGameState( networkController.getGameId(), networkController.getPlayerId()));
		
		while(!networkController.checkIfMyTurn(state)) {
			state = new GameStateData(networkController.getGameState( networkController.getGameId(), networkController.getPlayerId()));
			networkController.checkIfMyTurn(state);
		}*/
		
		/*GameStateData state = new GameStateData(networkController.getGameState( networkController.getGameId(), pl1));
		
		
		if(!networkController.checkIfMyTurn(state)) {
			networkController.sendMap(mapController.getMyMap(),networkController.getPlayerId());

		}*/
		//state = new GameStateData(networkController.getGameState( networkController.getGameId(), networkController.getPlayerId()));
		//while(!networkController.checkIfMyTurn(state)) {
			//mapController.generateMap();

		//	state = new GameStateData(networkController.getGameState( networkController.getGameId(), networkController.getPlayerId()));
		//	networkController.checkIfMyTurn(state);
		//}
		/*state = new GameStateData(networkController.getGameState( networkController.getGameId(), pl1));
		if(networkController.checkIfMyTurn(state)) {
			mapController.generateMap();
			networkController.sendMap(mapController.getMyMap(), pl1);
		}*/
		
		
		// print halfmap
		//ui.printMap(mapController.getMyMap());
		

		//second
		
		
		
		
		/*state = new GameStateData(networkController.getGameState( networkController.getGameId(), pl2));
		
		while(!networkController.checkIfMyTurn(state)) {
			state = new GameStateData(networkController.getGameState( networkController.getGameId(), pl2));
			networkController.checkIfMyTurn(state);
		}*/
		
		// print halfmap
		//ui.printMap(mapController.getMyMap());
		
	}
	
	
	
	
	
}
