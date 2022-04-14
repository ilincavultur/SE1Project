package client.controllers;

import MessagesBase.MessagesFromClient.PlayerRegistration;
import client.models.gameData.GameStateData;
import client.models.mapData.ClientMap;
import client.network.Network;
import client.network.NetworkConverter;

public class NetworkController {
	
	
	
	Network network;
	NetworkConverter networkConverter;

	public NetworkController(String gameId, String serverBaseUrl) {
		super();
		this.network = new Network(gameId, serverBaseUrl);
		this.networkConverter = new NetworkConverter();
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
	
	void sendMap(ClientMap map) {
		network.sendMap(networkConverter.convertMapTo(network.getPlayerID(), map));
	}
	
	
	
}
