package client.controllers;

import MessagesBase.MessagesFromClient.PlayerRegistration;
import MessagesBase.MessagesFromServer.GameState;
import client.models.gameData.GameStateData;
import client.models.gameData.enums.ClientPlayerState;
import client.models.mapData.ClientMap;
import client.movement.enums.MoveCommand;
import client.network.Network;
import client.network.NetworkConverter;

public class NetworkController {
	
	
	
	private Network network;
	private NetworkConverter networkConverter;

	public NetworkController(String gameId, String serverBaseUrl) {
		super();
		this.network = new Network(gameId, serverBaseUrl);
		this.networkConverter = new NetworkConverter();
	}
	
	

	public Network getNetwork() {
		return network;
	}



	public void setNetwork(Network network) {
		this.network = network;
	}



	public NetworkConverter getNetworkConverter() {
		return networkConverter;
	}



	public void setNetworkConverter(NetworkConverter networkConverter) {
		this.networkConverter = networkConverter;
	}



	String getPlayerId() {
		return network.getPlayerID();
	}
	
	String getGameId() {
		return network.getGameID();
	}

	void registerPlayer(PlayerRegistration playerReg) {
		network.registerPlayer(playerReg);
	}
	
	GameStateData getGameState(String gameId, String playerId) {
		GameStateData gsd = new GameStateData(networkConverter.convertGameStateFrom(network.getGameState(gameId, playerId)));
		return gsd;
	}
	
	boolean checkIfMyTurn(String playerId) {
		
		//GameStateData state = networkConverter.convertGameStateFrom(network.getGameState(network.getGameID(), network.getPlayerID()));
		GameStateData state = networkConverter.convertGameStateFrom(network.getGameState(network.getGameID(), playerId));
		
		return state.getPlayerState() == ClientPlayerState.MUSTACT;
	
	}
	
	void sendMap(ClientMap map, String plID) {
		//for test
		network.sendMap(networkConverter.convertMapTo(plID, map));
		//network.sendMap(networkConverter.convertMapTo(network.getPlayerID(), map));
	}
	
	void sendMove(String playerId, MoveCommand move) {
		network.sendMove(networkConverter.convertMoveTo(playerId, move));
	}
	
	
	
}
