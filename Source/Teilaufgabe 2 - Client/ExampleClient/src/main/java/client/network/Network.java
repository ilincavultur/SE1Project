package client.network;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import MessagesBase.ResponseEnvelope;
import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.ERequestState;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import MessagesBase.MessagesFromServer.GameState;
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

	public void registerPlayer(PlayerRegistration playerReg) {
		
		UniquePlayerIdentifier uniqueID = new UniquePlayerIdentifier();
		
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + this.gameID + "/players")
				.body(BodyInserters.fromValue(playerReg)) // specify the data which is sent to the server
				.retrieve().bodyToMono(ResponseEnvelope.class); // specify the object returned by the server

		ResponseEnvelope<UniquePlayerIdentifier> resultReg = webAccess.block();

		if (resultReg.getState() == ERequestState.Error) {
			System.err.println("Client error, errormessage: " + resultReg.getExceptionMessage());
		} else {
			uniqueID = resultReg.getData().get();
			this.playerID = uniqueID.getUniquePlayerID();
			System.out.println("My Player ID: " + uniqueID.getUniquePlayerID());
		}
				
	}
	
	public GameState getGameState(String gameId, String playerId) {
			// you will need to fill the variables with the appropriate information
			//String baseUrl = "UseValueFromARGS_1 FROM main";
			//String gameId = "UseValueFromARGS_2 FROM main";
			//String playerId = "From the client registration";
			//String baseUrl = "http://swe1.wst.univie.ac.at";
		//	String gameId = "6sEof";
		//	String playerId = "fbb8d7a8-66f0-43da-bc06-35e14e04c87a";
			//second player fbb8d7a8-66f0-43da-bc06-35e14e04c87a
			//27fe2f56-a8ab-4f75-ac9e-3a48e170a1c6
			// TIP: Use a global instance of the base WebClient throughout each
			// communication
			// you can init it once in the CTOR and use it in each of the network
			// communication methods in your networking class
		
		System.out.println(gameId);

			Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.GET)
					.uri("/" + gameId + "/states/" + playerId).retrieve().bodyToMono(ResponseEnvelope.class); // specify the
																												// object
																												// returned
																												// by the
																												// server

			// WebClient support asynchronous message exchange. In SE1 we use a synchronous
			// one for the sake of simplicity. So calling block is fine.
			ResponseEnvelope<GameState> requestResult = webAccess.block();

			// always check for errors, and if some are reported, at least print them to the
			// console (logging should always be preferred!)
			// so that you become aware of them during debugging! The provided server gives
			// you constructive error messages.
			if (requestResult.getState() == ERequestState.Error) {
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
			System.err.println("Client error, errormessage: " + resultReg.getExceptionMessage());
		} else {
			System.out.println("half map was correct");
		}
		
		
	}
}
