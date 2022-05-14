package client.network;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import MessagesBase.ResponseEnvelope;
import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.EMove;
import MessagesBase.MessagesFromClient.ERequestState;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import MessagesBase.MessagesFromServer.GameState;
import client.exceptions.NetworkException;
import reactor.core.publisher.Mono;


public class Network {
	
	private String serverBaseUrl;
	
	private WebClient baseWebClient;
	
	private String gameID;
	
	private String playerID;

	private Instant gameDataRequestTimestamp;
	
	public String getServerBaseUrl() {
		return serverBaseUrl;
	}

	public void setServerBaseUrl(String serverBaseUrl) {
		this.serverBaseUrl = serverBaseUrl;
	}

	public WebClient getBaseWebClient() {
		return baseWebClient;
	}

	public void setBaseWebClient(WebClient baseWebClient) {
		this.baseWebClient = baseWebClient;
	}

	public String getGameID() {
		return gameID;
	}

	public void setGameID(String gameID) {
		this.gameID = gameID;
	}

	public String getPlayerID() {
		return playerID;
	}

	public void setPlayerID(String playerID) {
		this.playerID = playerID;
	}

	public Network(String gameID, String serverBaseURL) {
		
		super();
		this.serverBaseUrl = serverBaseURL;
		this.baseWebClient = WebClient.builder().baseUrl(serverBaseURL + "/games")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE) 																	
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE).build();
		this.gameID = gameID;
	}
	
	boolean canMakeNewRequest() {
		if (gameDataRequestTimestamp == null) {
			return true;
		}
		Duration dur = Duration.between(gameDataRequestTimestamp, Instant.now());
		return dur.getSeconds() > 0.4;
	}

	public void registerPlayer(PlayerRegistration playerReg) {
		
		UniquePlayerIdentifier uniqueID = new UniquePlayerIdentifier();
		
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + this.gameID + "/players")
				.body(BodyInserters.fromValue(playerReg)) // specify the data which is sent to the server
				.retrieve().bodyToMono(ResponseEnvelope.class); // specify the object returned by the server

		ResponseEnvelope<UniquePlayerIdentifier> resultReg = webAccess.block();

		if (resultReg.getState() == ERequestState.Error) {
			//throw new NetworkException("Client error, errormessage: " + resultReg.getExceptionMessage());
			System.err.println("Client error, errormessage: " + resultReg.getExceptionMessage());
		} else {
			uniqueID = resultReg.getData().get();
			this.playerID = uniqueID.getUniquePlayerID();
			System.out.println("My Player ID: " + uniqueID.getUniquePlayerID());
		}
				
	}
	
	public GameState getGameState(String gameId, String playerId) {

			while (!canMakeNewRequest()) {
				//wait
			}
			gameDataRequestTimestamp = Instant.now();
		
			

			Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.GET)
					.uri("/" + gameId + "/states/" + playerId).retrieve().bodyToMono(ResponseEnvelope.class); 
				
			ResponseEnvelope<GameState> requestResult = webAccess.block();

			if (requestResult.getState() == ERequestState.Error) {
				//throw new NetworkException("Client error, errormessage: " + requestResult.getExceptionMessage());
				System.err.println("Client error, errormessage: " + requestResult.getExceptionMessage());
			} 
		return requestResult.getData().get();
			
		
	}
	
	public void sendMap(HalfMap networkHalfMap) {
		
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + this.gameID + "/halfmaps")
				.body(BodyInserters.fromValue(networkHalfMap)) 
				.retrieve().bodyToMono(ResponseEnvelope.class); 

		ResponseEnvelope resultReg = webAccess.block();

		if (resultReg.getState() == ERequestState.Error) {
			//throw new NetworkException("Client error, errormessage: " + resultReg.getExceptionMessage());
			System.err.println("Client error, errormessage: " + resultReg.getExceptionMessage());
		} else {
			System.out.println("half map was correct");
		}
		
		
	}
	
	public void sendMove(PlayerMove networkMove) {
		
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + this.gameID + "/moves")
				.body(BodyInserters.fromValue(networkMove)) 
				.retrieve().bodyToMono(ResponseEnvelope.class); 

		ResponseEnvelope resultReg = webAccess.block();

		if (resultReg.getState() == ERequestState.Error) {
			//throw new NetworkException("Client error, errormessage: " + resultReg.getExceptionMessage());
			System.err.println("Client error, errormessage: " + resultReg.getExceptionMessage());
		} else {
			System.out.println("move was correct");
		}
		
		
	}
}
