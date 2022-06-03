package server.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import server.models.GameState;
import server.network.NetworkConverter;

public class GameStateController {

	private Map<String, GameState> games = new HashMap<String, GameState>();
	private NetworkConverter networkConverter = new NetworkConverter();
	
	
	
	public Map<String, GameState> getGames() {
		return games;
	}

	public void setGames(Map<String, GameState> games) {
		this.games = games;
	}

	// SCHIMBA
	public UniqueGameIdentifier createUniqueGameId() {
		
		String toReturn = null;
		
		Random randomNo = new Random();
		String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int no = alphabet.length();
		
		while (toReturn == null || games.containsKey(toReturn)) {
			toReturn = "";
		
			for (int i=0; i<5; i++) {
				int nextCharacter = randomNo.nextInt(no);
				toReturn += alphabet.charAt(nextCharacter);
			}
			
		}
		
		UniqueGameIdentifier toRet = new UniqueGameIdentifier(toReturn);
		
		return toRet;
	}
	
	public UniquePlayerIdentifier createUniquePlayerId() {
		
		UniquePlayerIdentifier toRet = new UniquePlayerIdentifier(UUID.randomUUID().toString());
		
		return toRet;
	}
	
	public void createNewGame() {
		
	}
	
	public void registerPlayer (UniqueGameIdentifier gameId, PlayerRegistration playerReg) {
		
	}
	
	// validate done in servernedpoints
	public void receiveHalfMap() {
		
		
		// translate
		
		// save
	}
	
	//validate done in servernedpoints
	public void requestGameState() {
	
		
		// translate
		
		// save
	}
	
	//validate done in servernedpoints
	public void receiveMove() {
		
		// translate
		
	}
}
