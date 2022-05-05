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
		//gameStateData = gStateData;
		//GameStateData newGSD = gStateData;
		this.gameStateData.setGameStateId(newGSD.getGameStateId());
		this.gameStateData.setHasCollectedTreasure(newGSD.getHasCollectedTreasure());
		this.gameStateData.setPlayerState(newGSD.getPlayerState());
		if(newGSD.getFullMap() != null) {
			this.gameStateData.setFullMap(newGSD.getFullMap());
			this.gameStateData.setPlayerPosition(newGSD.getPlayerPosition());
			this.moveController.setFullMap(newGSD.getFullMap());
			this.moveController.setCurrentField(newGSD.getPlayerPosition());
			this.moveController.getPathCalc().setMyMap(newGSD.getFullMap());
		}
		this.moveController.setGameState(newGSD);
		
		logger.info("updategamestatedata");
		
		//this.gameStateData = gStateData;


		
	}
	
	public void startGame() {
		
		
		registerPlayer();
		
		String pl1 = networkController.getNetwork().getPlayerID();
	
		createAndSendMap(pl1);
		
		receiveFullMap();
		
		
		//moveController.setUp();
		//test
		//moveController.calcMovesToGoal();
		
		/*for (int i=0; i<moveController.getMovesList().size(); i++) {
			System.out.println("AICIIIII S PATH");
			System.out.println (moveController.getMovesList().get(i).toString() + " " + moveController.getMovesList().get(i).toString());
	
		}*/
		//moveController.getNextMove();
		play(pl1);
		logger.info("functia play a terminat");
		if (this.gameStateData.getPlayerPosition() != null) {
			
			logger.info("blanalnalala");	
			System.out.println("acum suntem aici : " + this.gameStateData.getMyCurrentPosition(this.gameStateData.getFullMap()).getPosition().getX() + " " + this.gameStateData.getMyCurrentPosition(this.gameStateData.getFullMap()).getPosition().getY());
		}
		
		ClientPlayerState sst = this.gameStateData.getPlayerState();
		logger.info("you" + sst.toString());
		//System.out.println("acum suntem aici : " + this.gameStateData.getPlayerPosition().getX() + " " + this.gameStateData.getPlayerPosition().getY());
		//networkController.getGameState(networkController.getGameId(), pl1).getPlayerPosition()
		/*if(gameStateData.getPlayerPosition() == null) {
			System.out.println("e nullllll");
		}else {
			System.out.println("acum suntem aici : " + gameStateData.getPlayerPosition().getX() + " " + gameStateData.getPlayerPosition().getY());

		}*/
		
		System.exit(0);
		//endGame();
		
		

	}
	
	public void registerPlayer() {
		PlayerRegistration playerReg = new PlayerRegistration("Ilinca", "Vultur",
				"ilincav00");
		
		// register Players - network
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
		
		logger.info("Map has been sent & was correct");
		
	}
	
	public void receiveFullMap() {
		//GameStateData state = new GameStateData(networkController.getGameState( networkController.getGameId(), networkController.getPlayerId()));
		updateGameStateData(networkController.getGameState(networkController.getGameId(), networkController.getPlayerId()));
		//moveController.setFullMap(state.getFullMap());
		//moveController.getPathCalc().setMyMap(state.getFullMap());
		//moveController.setCurrentField(state.getMyCurrentPosition(state.getFullMap()).getPosition());
		
		System.out.println("my fort:" + mapController.getMyFortField().getPosition().getX() + " "+ mapController.getMyFortField().getPosition().getY());
		//System.out.println("my fort based on the data:" +this.gameStateData.getMyCurrentPosition(this.gameStateData.getFullMap()).getPosition().getX() + " "+ this.gameStateData.getMyCurrentPosition(this.gameStateData.getFullMap()).getPosition().getY());
		System.out.println("my fort based on the data:" +this.gameStateData.getPlayerPosition().getX() + " "+ this.gameStateData.getPlayerPosition().getY());

		ui.printMap(this.gameStateData.getFullMap());
		
		logger.info("Full Map has been received");
		
	}
	
	public void play(String pl1) {
		while (this.gameStateData.getPlayerState() != ClientPlayerState.LOST && this.gameStateData.getPlayerState() != ClientPlayerState.WON) {
			while(!networkController.checkIfMyTurn(pl1)) {
				updateGameStateData(networkController.getGameState(networkController.getGameId(), pl1));
				// update path
				moveController.updatePath();
		
			}
			
			logger.info("e randul meu");
			MoveCommand newMove = moveController.getNextMove();
			if (newMove != null) {
				logger.info("newMove nu e null!");
				networkController.sendMove(pl1, newMove);	
				
				updateGameStateData(networkController.getGameState(networkController.getGameId(), pl1));
				// update path
				moveController.updatePath();
				
				if (this.gameStateData.getHasCollectedTreasure() !=null) {
					//boolean letsee = this.gameStateData.getHasCollectedTreasure();	
					logger.info("ailuat treasureu");
				}
				
				
				
				System.out.println("acum suntem aici in gamestatecontroller: " + this.gameStateData.getPlayerPosition().getX() + " " + this.gameStateData.getPlayerPosition().getY());

				++moves;
			} else {
				return;
			}
			//ui.printMap(moveController.getFullMap());

			
		}
		ui.printMap(this.gameStateData.getFullMap());
		
	}
	
	public void endGame() {
		if(moves >=100 || this.gameStateData.getPlayerState() == ClientPlayerState.LOST || this.gameStateData.getPlayerState() != ClientPlayerState.WON) {
			logger.info(this.gameStateData.getPlayerState().toString());
			System.exit(0);		
		}
	
	}
	
	
	
}
