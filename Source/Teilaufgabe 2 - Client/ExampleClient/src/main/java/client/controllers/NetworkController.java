package client.controllers;

import MessagesBase.MessagesFromClient.PlayerRegistration;
import client.models.mapData.ClientMap;
import client.network.Network;
import client.network.NetworkConverter;

public class NetworkController {
	
	PlayerRegistration playerReg = new PlayerRegistration("Ilinca", "Vultur",
			"ilincav00");
	
	Network network;
	NetworkConverter networkConverter;

	public NetworkController(String gameId, String serverBaseUrl) {
		super();
		this.network = new Network(gameId, serverBaseUrl);
		this.networkConverter = new NetworkConverter();
	}


	void registerPlayer(PlayerRegistration playerReg) {
		network.registerPlayer(playerReg);
	}
	
	void sendMap(ClientMap map) {
		this.network.sendMap(this.networkConverter.convertMapTo(this.network.getPlayerID(), map));
	}
	
	
	
	
}
