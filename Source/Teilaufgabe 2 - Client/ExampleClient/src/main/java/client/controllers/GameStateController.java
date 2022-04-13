package client.controllers;

import MessagesBase.MessagesFromClient.PlayerRegistration;
import client.ui.CLI;

public class GameStateController {
	
	
	
	NetworkController networkController;
	MapController mapController;
	MovementController moveController;
	PlayerController playerController;
	CLI ui;
	//constructor in the gamestatecontroller where you initialise your network classes and 
	//based on the constructor it ends up in the network class

	public GameStateController(String gameId, String serverBaseUrl) {
		super();
		this.networkController = new NetworkController(gameId, serverBaseUrl);
		this.mapController = new MapController();
		this.moveController = new MovementController();
		this.playerController = new PlayerController();
		this.ui = new CLI();
	}
	
	public void startGame() {
		
		// register Player
		
		// generate halfmap & send halfmap
		
		
		
	}
	
	
	
	
	
}
