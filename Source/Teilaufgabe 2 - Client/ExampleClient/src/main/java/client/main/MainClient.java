package client.main;


import client.controllers.GameStateController;


public class MainClient {

	public static void main(String[] args) {
		
		String serverBaseUrl = args[1];
		String gameId = args[2];
		//String serverBaseUrl = "http://swe1.wst.univie.ac.at";
		//String gameId = "0qTbK";
		
		
		GameStateController gameController = new GameStateController(gameId, serverBaseUrl);
		
		gameController.startGame();

		
	}

	
}
