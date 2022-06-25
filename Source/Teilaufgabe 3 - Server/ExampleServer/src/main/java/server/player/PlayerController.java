package server.player;

import java.util.ArrayList;
import java.util.List;

import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.PlayerRegistration;

public class PlayerController {
	
	private List<Player> players = new ArrayList<Player>();
	
	public List<Player> getPlayers() {
		return players;
	}

	public void registerPlayer (UniquePlayerIdentifier playerId, PlayerRegistration playerReg) {
		this.players.add(new Player(playerId.getUniquePlayerID(), playerReg));
	}
	
	/*
	 * 	If not all players registered it means it is not player's turn to send map
	 *  Returns true if player's turn
	 */
	public boolean myTurn(String playerId, int idxPlayersTurn) {

		if (this.players.size() == 1) {
			return false;
		} else if (this.players.size() == 2) {
			if (players.get(0).getPlayerId().equals(playerId)) {
				if (idxPlayersTurn == 0) {
					return true;
				}
			} else if (players.get(1).getPlayerId().equals(playerId)) {
				if (idxPlayersTurn == 1) {
					return true;
				}
			}
		}
		
		return false;
	}

	public Player getPlayerWithId(String playerId) {
		Player toReturn = new Player();
		
		for (Player player: players) {
			if (player.getPlayerId().equals(playerId)) {
				toReturn = player;
				break;
			}
		}
		return toReturn;
	}
	
	/*
	 *  returns the player whose id is not equal to the parameter
	 */
	public Player getTheOtherPlayer(String playerId) {
		Player toReturn = new Player();
		
		for (Player player: players) {
			if (!player.getPlayerId().equals(playerId)) {
				toReturn = player;
				break;
			}
		}
		return toReturn;
	}
	
	public boolean bothHalfMapsPresent() {
		
		for (Player player : players) {
			if (player.getHalfMap().isEmpty()) {
				return false;
			}
		}
		
		return true;
	}
}
