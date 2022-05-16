package client.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesBase.MessagesFromClient.PlayerRegistration;
import client.exceptions.NetworkException;
import client.models.gameData.GameStateData;
import client.models.gameData.enums.ClientPlayerState;
import client.models.mapData.ClientMap;
import client.movement.enums.MoveCommand;
import client.network.Network;
import client.network.NetworkConverter;

public class NetworkController {
	
	private Network network;
	private NetworkConverter networkConverter;
	private static final Logger logger = LoggerFactory.getLogger(NetworkController.class);

	public NetworkController(String gameId, String serverBaseUrl) {
		super();
		this.network = new Network(gameId, serverBaseUrl);
		this.networkConverter = new NetworkConverter();
	}
	
	public Network getNetwork() {
		return network;
	}

	String getPlayerId() {
		return network.getPlayerID();
	}
	
	String getGameId() {
		return network.getGameID();
	}

	public void registerPlayer(PlayerRegistration playerReg) {
		try {
			network.registerPlayer(playerReg);
		} catch (NetworkException e) {
			logger.info("");
			
		}
	}
	
	public GameStateData getGameState(String gameId, String playerId) {
		
		GameStateData gsd = new GameStateData();
		
		try {
			gsd = networkConverter.convertGameStateFrom(network.getGameState(gameId, playerId));
		} catch (NetworkException e) {
		
			e.printStackTrace();
		}

		return gsd;
	}
	
	public boolean checkIfMyTurn(String playerId) {
				
		GameStateData state = new GameStateData();
		try {
			
			state = networkConverter.convertGameStateFrom(network.getGameState(network.getGameID(), playerId));
		} catch (NetworkException e) {
		
			e.printStackTrace();
		}
		
		return state.getPlayerState() == ClientPlayerState.MUSTACT;
	
	}
	
	public void sendMap(ClientMap map, String plID) {

		try {
			network.sendMap(networkConverter.convertMapTo(plID, map));
		} catch (NetworkException e) {
		
			e.printStackTrace();
		}
		
	}
	
	public void sendMove(String playerId, MoveCommand move) {
		try {
			network.sendMove(networkConverter.convertMoveTo(playerId, move));
		} catch (NetworkException e) {
		
			e.printStackTrace();
		}
	}
		
}
