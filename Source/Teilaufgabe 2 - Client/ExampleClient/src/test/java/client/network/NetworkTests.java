package client.network;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mockito;

import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import client.controllers.GameStateController;
import client.controllers.MapController;
import client.controllers.NetworkController;
import client.exceptions.NetworkException;
import client.models.mapData.ClientMap;
import client.models.mapData.ClientMapGenerator;
import client.models.mapData.MapValidator;

public class NetworkTests {
	/*
	@Test
	public void GameWasCreated_RegisterClient_ExpectedReceivedUniqueId() {
		
		//arrange
		Network network = Mockito.mock(Network.class);
		NetworkController networkController = Mockito.mock(NetworkController.class);
		GameStateController gameStateController = Mockito.mock(GameStateController.class);
		ClientMap myMap = new ClientMap();
		NetworkConverter converter = Mockito.mock(NetworkConverter.class);
		PlayerRegistration pr = new PlayerRegistration("Ilinca", "Vultur",
				"ilincav00");
	
		//act
		//gameStateController.registerPlayer();
	
		
		
		verify(networkController).registerPlayer(pr);
		
	}
	*/
	
}
