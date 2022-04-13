package client.network;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import MessagesBase.ResponseEnvelope;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.ERequestState;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import reactor.core.publisher.Mono;


public class Network {
	
	String serverBaseUrl;
	
	WebClient baseWebClient;
	
	String gameID;
	
	String playerID;

	
	
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

	public UniquePlayerIdentifier registerPlayer(PlayerRegistration playerReg) {
		
		UniquePlayerIdentifier uniqueID = new UniquePlayerIdentifier();
		
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + this.gameID + "/players")
				.body(BodyInserters.fromValue(playerReg)) // specify the data which is sent to the server
				.retrieve().bodyToMono(ResponseEnvelope.class); // specify the object returned by the server

		ResponseEnvelope<UniquePlayerIdentifier> resultReg = webAccess.block();

		if (resultReg.getState() == ERequestState.Error) {
			System.err.println("Client error, errormessage: " + resultReg.getExceptionMessage());
		} else {
			uniqueID = resultReg.getData().get();
			System.out.println("My Player ID: " + uniqueID.getUniquePlayerID());
		}
		
		this.playerID = uniqueID.getUniquePlayerID();
		
		return uniqueID;
		
	}
	
	public void sendMap(HalfMap networkHalfMap) {
		
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + this.gameID + "/halfmaps")
				.body(BodyInserters.fromValue(networkHalfMap)) 
				.retrieve().bodyToMono(ResponseEnvelope.class); 

		ResponseEnvelope<UniquePlayerIdentifier> resultReg = webAccess.block();

		if (resultReg.getState() == ERequestState.Error) {
			System.err.println("Client error, errormessage: " + resultReg.getExceptionMessage());
		} else {
			System.out.println("half map was correct");
		}
		
		
	}
}
