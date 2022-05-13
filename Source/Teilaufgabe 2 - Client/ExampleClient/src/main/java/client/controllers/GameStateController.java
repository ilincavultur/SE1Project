package client.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesBase.MessagesFromClient.PlayerRegistration;
import client.models.gameData.GameStateData;
import client.models.gameData.enums.ClientPlayerState;
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
		if(newGSD.getFullMap() != null) {
			this.mapController.setMyMap(newGSD.getFullMap());
			this.gameStateData.setFullMap(newGSD.getFullMap());
			this.gameStateData.setPlayerPosition(newGSD.getPlayerPosition());
			this.moveController.setFullMap(newGSD.getFullMap());
			this.moveController.setCurrentField(newGSD.getPlayerPosition());
			this.moveController.getPathCalc().setMyMap(newGSD.getFullMap());
		}
		this.moveController.setGameState(newGSD);
	
	}
	
	public void startGame() {
		
		registerPlayer();
		
		String pl1 = networkController.getNetwork().getPlayerID();
	
		createAndSendMap(pl1);
		
		receiveFullMap();
		
		
		moveController.setUp();
		moveController.updatePath();

		play(pl1);
		
		logger.info("play function has ended");
		
		//------------------------- test print
		if (this.gameStateData.getPlayerPosition() != null) {
			
			logger.info("blanalnalala");	
			System.out.println("acum suntem aici : " + this.gameStateData.getMyCurrentPosition(this.gameStateData.getFullMap()).getPosition().getX() + " " + this.gameStateData.getMyCurrentPosition(this.gameStateData.getFullMap()).getPosition().getY());
		}
		//------------------------- test print
		
		
		// aici trebuie endgame
		endGame();

	}
	
	public void registerPlayer() {
		
		PlayerRegistration playerReg = new PlayerRegistration("Ilinca", "Vultur",
				"ilincav00");
		
		networkController.registerPlayer(playerReg);
		
	}
	
	public void createAndSendMap(String pl1) {
		
		mapController.generateMap();
		
		logger.info("Map has been generated");

	
		while(!networkController.checkIfMyTurn(pl1)) {
			updateGameStateData(networkController.getGameState(networkController.getGameId(), pl1));
		}

		if (this.gameStateData.getPlayerState() != ClientPlayerState.LOST && this.gameStateData.getPlayerState() != ClientPlayerState.WON) {
			networkController.sendMap(mapController.getMyMap(),pl1);
			++moves;
		} else {
			return;
		}
		
		gameStateData.registerInterestedView(ui);
		
		logger.info("Map has been sent & was correct");
		
	}
	
	public void receiveFullMap() {
		
		updateGameStateData(networkController.getGameState(networkController.getGameId(), networkController.getPlayerId()));

		//------------------------- test print
		System.out.println("my fort:" + mapController.getMyFortField().getPosition().getX() + " "+ mapController.getMyFortField().getPosition().getY());
		System.out.println("my fort based on the data:" +this.gameStateData.getPlayerPosition().getX() + " "+ this.gameStateData.getPlayerPosition().getY());
		//------------------------- test print

		logger.info("Full Map has been received");
		
	}
	
	public void play(String pl1) {
		
		while (this.gameStateData.getPlayerState() != ClientPlayerState.LOST && this.gameStateData.getPlayerState() != ClientPlayerState.WON) {
			
			while(!networkController.checkIfMyTurn(pl1)) {
				
				updateGameStateData(networkController.getGameState(networkController.getGameId(), pl1));
				// update path
				moveController.updatePath();
		
			}
			
			MoveCommand newMove = moveController.getNextMove();
			
			if (newMove != null) {
				
				networkController.sendMove(pl1, newMove);	
				
				updateGameStateData(networkController.getGameState(networkController.getGameId(), pl1));
				
				if (this.gameStateData.getTreasureIsPresentAt() !=null && this.gameStateData.getHasCollectedTreasure() != null && this.gameStateData.getHasCollectedTreasure() == false) {
					
					moveController.setGoPickUpTreasure(true);
					logger.info("treasure has been picked up");
					
				} else {
					
					moveController.setGoPickUpTreasure(false);
					
				}
				if (this.gameStateData.getEnemyFortIsPresentAt() !=null) {
				
					moveController.setGoPickUpTreasure(false);
					moveController.setGoBribeFort(true);
					logger.info("enemy fort is present");
					
				}
				
				// update path
				moveController.updatePath();
				
				updateGameStateData(networkController.getGameState(networkController.getGameId(), pl1));
				
				//------------------------- test print
				System.out.println("acum suntem aici in gamestatecontroller: " + this.gameStateData.getPlayerPosition().getX() + " " + this.gameStateData.getPlayerPosition().getY());
				//------------------------- test print
				
				++moves;
			} //else {
				//moveController.updatePath();
			//}
			
		}
		
	}
	
	public void endGame() {
		
		if(moves >=100 || this.gameStateData.getPlayerState() == ClientPlayerState.LOST || this.gameStateData.getPlayerState() != ClientPlayerState.WON) {
			if (moves >= 100) {
				System.out.println("100 moves reached ");
			}
			
			if(this.gameStateData.getPlayerState() ==ClientPlayerState.LOST) {
			
				System.out.println("You Lost :( ");	
			}
			if(this.gameStateData.getPlayerState() ==ClientPlayerState.WON) {
				System.out.println("You Won!!! ");	
			}

			System.exit(0);		
		}
	
	}
	
	
	
}
