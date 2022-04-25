package client.controllers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesBase.MessagesFromClient.PlayerRegistration;
import client.models.gameData.GameStateData;
import client.models.gameData.enums.ClientPlayerState;
import client.models.mapData.Coordinates;
import client.models.mapData.MapField;
import client.models.mapData.enums.FortState;
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
	
	public void updateGameStateData(GameStateData gStateData) {
		this.gameStateData.setGameStateId(gStateData.getGameStateId());
		this.gameStateData.setHasCollectedTreasure(gStateData.getHasCollectedTreasure());
		this.gameStateData.setPlayerState(gStateData.getPlayerState());
		this.gameStateData.setFullMap(gStateData.getFullMap());
		this.moveController.setCurrentField(gStateData.getMyCurrentPosition());
		this.moveController.setFullMap(gStateData.getFullMap());
	}
	
	public void startGame() {
		
		
		registerPlayer();
		
		String pl1 = networkController.getNetwork().getPlayerID();
	
		createAndSendMap(pl1);
		
		receiveFullMap();
		
		//test
		moveController.calcMovesToGoal();
		
		/*for (int i=0; i<moveController.getMovesList().size(); i++) {
			System.out.println("AICIIIII S PATH");
			System.out.println (moveController.getMovesList().get(i).toString() + " " + moveController.getMovesList().get(i).toString());
	
		}*/
		//moveController.getNextMove();
		play(pl1);
		
		System.out.println("acum suntem pe nodul" + moveController.getCurrentField().getPosition().getX() + " " + moveController.getCurrentField().getPosition().getY());
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
			
		if (this.gameStateData.getPlayerState() != ClientPlayerState.LOST || this.gameStateData.getPlayerState() != ClientPlayerState.WON) {
			networkController.sendMap(mapController.getMyMap(),pl1);
			++moves;
		} else {
			return;
		}
		
		//networkController.sendMap(mapController.getMyMap(),pl1);
		logger.info("Map has been sent & was correct");
		
	}
	
	public void receiveFullMap() {
		GameStateData state = new GameStateData(networkController.getGameState( networkController.getGameId(), networkController.getPlayerId()));
		
		moveController.setFullMap(state.getFullMap());
		moveController.getPathCalc().setMyMap(state.getFullMap());
		moveController.setCurrentField(mapController.getMyFortField());
		
		System.out.println("my fort:" + mapController.getMyFortField().getPosition().getX() + " "+ mapController.getMyFortField().getPosition().getY());
		
		ui.printMap(state.getFullMap());
		
		logger.info("Full Map has been received");
		
	}
	
	public void play(String pl1) {
		do {
			while(!networkController.checkIfMyTurn(pl1)) {
				updateGameStateData(networkController.getGameState(networkController.getGameId(), pl1));
			}
			MoveCommand newMove = moveController.getNextMove();
			if (newMove != null) {
				//updateGameStateData(networkController.getGameState(networkController.getGameId(), pl1));
				networkController.sendMove(pl1, newMove);	
				++moves;
			} else {
				return;
			}
			//ui.printMap(moveController.getFullMap());

			
		} while (this.gameStateData.getPlayerState() != ClientPlayerState.LOST || this.gameStateData.getPlayerState() != ClientPlayerState.WON);
		
		
	}
	
	public void endGame() {
		if(moves >=100 || this.gameStateData.getPlayerState() == ClientPlayerState.LOST || this.gameStateData.getPlayerState() != ClientPlayerState.WON) {
			System.exit(0);		
		}
	
	}
	
	
	
}
