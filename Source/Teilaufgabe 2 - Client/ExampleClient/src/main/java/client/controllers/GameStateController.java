package client.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import client.models.gameData.GameStateData;
import client.models.gameData.enums.ClientPlayerState;
import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.movement.enums.MoveCommand;
import client.ui.CLI;

public class GameStateController {
	
	private static final Logger logger = LoggerFactory.getLogger(GameStateController.class);
	
	//private GameStateData gameStateData;
	private NetworkController networkController;
	private MapController mapController;
	private MovementController moveController;
	private PlayerController playerController;
	private CLI ui;
	

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
	
		logger.info("Player has registered");
		//------------------MINI DEADLINE-----------------
		
		// register Players - network
		networkController.registerPlayer(playerReg);
		String pl1 = networkController.getNetwork().getPlayerID();
		System.out.println(pl1);
		networkController.registerPlayer(playerReg);
		String pl2 = networkController.getNetwork().getPlayerID();
		System.out.println(pl2);
		
		mapController.generateMap();
		logger.info("Map has been generated");
		
		GameStateData state = new GameStateData(networkController.getGameState( networkController.getGameId(), pl1));
		// send halfmap - network
		if(state.getPlayerState() == ClientPlayerState.MUSTACT) {
			networkController.sendMap(mapController.getMyMap(),pl1);
			state = new GameStateData(networkController.getGameState( networkController.getGameId(), pl2));
			mapController.generateMap();
			if (state.getPlayerState() == ClientPlayerState.MUSTACT) {
				networkController.sendMap(mapController.getMyMap(),pl2);
			}
		} else {
			networkController.sendMap(mapController.getMyMap(),pl2);
			state = new GameStateData(networkController.getGameState( networkController.getGameId(), pl1));
			mapController.generateMap();
			if (state.getPlayerState() == ClientPlayerState.MUSTACT) {
				networkController.sendMap(mapController.getMyMap(),pl1);
			}
		}
		
		logger.info("Map has been sent & was correct");
		
		
		//this.gameStateData = state;
		//ui.printMap(gameStateData.getFullMap());
		
		/*state = new GameStateData(networkController.getGameState( networkController.getGameId(), pl1));
		
		while (!state.getPlayerState().equals(ClientPlayerState.LOST) && !state.getPlayerState().equals(ClientPlayerState.WON)) {
			//state = new GameStateData(networkController.getGameState( networkController.getGameId(), pl1));
			//TODO
			if (state.getPlayerState() == ClientPlayerState.MUSTACT) {
				networkController.sendMove(pl1, MoveCommand.DOWN);
				logger.info("Move has been sent");

			} else {
				state = new GameStateData(networkController.getGameState( networkController.getGameId(), pl1));
			}
		}*/
		
		
		state = new GameStateData(networkController.getGameState( networkController.getGameId(), pl1));
		if(state.getPlayerState() == ClientPlayerState.MUSTACT) {
			networkController.sendMove(pl1, MoveCommand.DOWN);
			state = new GameStateData(networkController.getGameState( networkController.getGameId(), pl2));
			if (state.getPlayerState() == ClientPlayerState.MUSTACT) {
				networkController.sendMove(pl2, MoveCommand.DOWN);
			}
		} else {
			networkController.sendMove(pl2, MoveCommand.DOWN);
			state = new GameStateData(networkController.getGameState( networkController.getGameId(), pl1));
			if (state.getPlayerState() == ClientPlayerState.MUSTACT) {
				networkController.sendMove(pl1, MoveCommand.DOWN);
			}
		}
		
		
		
		
		//------------------MINI DEADLINE----------------- 
		
		/*
		networkController.registerPlayer(playerReg);
		networkController.registerPlayer(playerReg);
		
		// generate halfmap - map
		mapController.generateMap();
		
		while(!networkController.checkIfMyTurn()) 
			
		
		networkController.sendMap(mapController.getMyMap(), networkController.getPlayerId());
		
		*/
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
		
		
		
		// print halfmap
		//ui.printMap(mapController.getMyMap());
		

	}
	
	
	
	
	
}
