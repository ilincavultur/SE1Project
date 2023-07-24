package client.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesBase.MessagesFromClient.PlayerRegistration;
import client.models.gameData.GameStateData;
import client.models.gameData.enums.ClientPlayerState;
import client.models.mapData.ClientMap;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.movement.enums.MoveCommand;
import client.ui.CLI;

public class GameStateController {
	
	private static final Logger logger = LoggerFactory.getLogger(GameStateController.class);
	
	private GameStateData gameStateData;
	private NetworkController networkController;
	private MapController mapController;
	private MovementController moveController;
	private CLI ui;
	private int moves = 0;
	private Coordinates currPos = new Coordinates(0,0);

	public GameStateController(String gameId, String serverBaseUrl) {
		super();
		this.gameStateData = new GameStateData();
		this.networkController = new NetworkController(gameId, serverBaseUrl);
		this.mapController = new MapController();
		this.moveController = new MovementController();
		this.ui = new CLI();
	}
	
	public void updateGameStateData(GameStateData newGSD) {
	
		this.gameStateData.setGameStateId(newGSD.getGameStateId());
		this.gameStateData.setHasCollectedTreasure(newGSD.getHasCollectedTreasure());
		this.gameStateData.setPlayerState(newGSD.getPlayerState());
		this.gameStateData.setGameStateId(newGSD.getGameStateId());
		if(newGSD.getFullMap() != null) {
			this.mapController.setMyMap(newGSD.getFullMap());
			if (this.currPos != null) {
				if (this.currPos.equals(newGSD.getPlayerPosition()) == false) {
					this.gameStateData.setFullMapPositionChanged(newGSD.getFullMap());	
				} else {
					this.gameStateData.setFullMap(newGSD.getFullMap());	
				}
			} else {
				this.gameStateData.setFullMapPositionChanged(newGSD.getFullMap());
			}
			//this.gameStateData.setFullMapPositionChanged(newGSD.getFullMap());
			this.gameStateData.setPlayerPosition(newGSD.getPlayerPosition());
			this.currPos = newGSD.getPlayerPosition();
			this.moveController.setFullMap(newGSD.getFullMap());
			this.moveController.setCurrentField(newGSD.getPlayerPosition());
			this.moveController.getPathCalc().setMyMap(newGSD.getFullMap());
		} else {
			this.gameStateData.setFullMap(null);
		}
		this.moveController.setGameState(newGSD);
	
	
	}
	
	public void startGame() {
		
		registerPlayer();
		
		String pl1 = networkController.getNetwork().getPlayerID();
	
		createAndSendMap(pl1);
		
		logger.info("Map has been sent & was correct");
		
		receiveFullMap();
		
		logger.info("Full Map has been received");
		
		moveController.setUp();
		moveController.updatePath();

		play(pl1);
		
		logger.info("play function has ended");
	
		endGame();
		
		System.exit(0);

	}
	
	private void registerPlayer() {
		
		PlayerRegistration playerReg = new PlayerRegistration("Ilinca", "Vultur",
				"ilincav00");
		
		networkController.registerPlayer(playerReg);
		
	}
	
	private void createAndSendMap(String pl1) {
		
		mapController.generateMap();
		
		ClientMap mapToSend = mapController.getMyMap();
		
		logger.info("Map has been generated");
		
		this.currPos.setX(0);
		this.currPos.setY(0);

		updateGameStateData(networkController.getGameState(networkController.getGameId(), pl1));
		System.out.println(this.gameStateData.getPlayerState().toString());
		while(this.gameStateData.getPlayerState() != ClientPlayerState.MUSTACT) {
			System.out.println(this.gameStateData.getPlayerState().toString());
			updateGameStateData(networkController.getGameState(networkController.getGameId(), pl1));
		}
		System.out.println("" + this.gameStateData.getPlayerState().toString());
		if (this.gameStateData.getPlayerState() == ClientPlayerState.MUSTACT) {
			networkController.sendMap(mapToSend,pl1);
			++moves;
		}
		
		gameStateData.registerInterestedView(ui);
		
	}
	
	private void receiveFullMap() {
		
		updateGameStateData(networkController.getGameState(networkController.getGameId(), networkController.getPlayerId()));
		
		while (this.gameStateData.getFullMap() == null) {
			logger.info("i still don t have the full map");
			updateGameStateData(networkController.getGameState(networkController.getGameId(), networkController.getPlayerId()));	
		}
	
		this.currPos = this.gameStateData.getPlayerPosition();
	
		
	}
	
	private void play(String pl1) {
		
		while (this.gameStateData.getPlayerState() != ClientPlayerState.LOST && this.gameStateData.getPlayerState() != ClientPlayerState.WON && moves < 100) {

			do {
				System.out.println(this.gameStateData.getPlayerState().toString());
				updateGameStateData(networkController.getGameState(networkController.getGameId(), pl1));
		
				moveController.updatePath();
			} while (this.gameStateData.getPlayerState() != ClientPlayerState.MUSTACT);
		
			MoveCommand newMove = moveController.getNextMove();
			
			if (newMove != null && this.gameStateData.getPlayerState() == ClientPlayerState.MUSTACT) {
				
				networkController.sendMove(pl1, newMove);	
				
				updateGameStateData(networkController.getGameState(networkController.getGameId(), pl1));
								
				// update path
				moveController.updatePath();
				
				updateGameStateData(networkController.getGameState(networkController.getGameId(), pl1));

				//------------------------- test print
				System.out.println("we are here now in gamestatecontroller: " + this.gameStateData.getPlayerPosition().getX() + " " + this.gameStateData.getPlayerPosition().getY());
				//------------------------- test print
				
				++moves;
			} 
	
			
		}

	}
	
	private void endGame() {
		
			if(this.gameStateData.getPlayerState() == ClientPlayerState.WON) {
				System.out.println("You Won!!! ");	
			}
			
			if(this.gameStateData.getPlayerState() == ClientPlayerState.LOST) {
				
				System.out.println("You Lost :( ");	
			}
			
			if (moves >= 100) {
				System.out.println("100 moves reached ");
			}			
	
	}
	
}
