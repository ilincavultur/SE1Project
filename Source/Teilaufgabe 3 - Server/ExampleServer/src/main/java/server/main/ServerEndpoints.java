package server.main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import MessagesBase.ResponseEnvelope;
import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesBase.MessagesFromClient.HalfMap;
import MessagesBase.MessagesFromClient.PlayerMove;
import MessagesBase.MessagesFromClient.PlayerRegistration;
import MessagesBase.MessagesFromServer.GameState;
import server.controllers.GameStateController;
import server.validation.PlayerIdRule;
import server.exceptions.GenericExampleException;
import server.exceptions.TooManyMapsSentException;
import server.models.InternalHalfMap;
import server.network.NetworkConverter;
import server.validation.BothPlayersRegisteredRule;
import server.validation.DontMoveIntoWaterRule;
import server.validation.DontMoveOutsideMapRule;
import server.validation.FieldsCoordinatesRule;
import server.validation.FortRule;
import server.validation.GameIdRule;
import server.validation.HalfMapSizeRule;
import server.validation.IRuleValidation;
import server.validation.MaxNoOfPlayersReachedRule;
import server.validation.MyTurnRule;
import server.validation.NoIslandsRule;
import server.validation.TerrainsNumberRule;
import server.validation.WaterOnEdgesRule;

@RestController
@RequestMapping(value = "/games")
public class ServerEndpoints {
	
	private GameStateController gameStateController = new GameStateController();
	private NetworkConverter networkConverter = new NetworkConverter();
	List<IRuleValidation> rules = List.of(new BothPlayersRegisteredRule(), new GameIdRule(), new DontMoveIntoWaterRule(), new DontMoveOutsideMapRule(), new FieldsCoordinatesRule(), new FortRule(),
			new HalfMapSizeRule(), new MaxNoOfPlayersReachedRule(), new MyTurnRule(), new NoIslandsRule(), new PlayerIdRule(), new TerrainsNumberRule(), new WaterOnEdgesRule());


	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody UniqueGameIdentifier newGame(
			@RequestParam(required = false, defaultValue = "false", value = "enableDebugMode") boolean enableDebugMode,
			@RequestParam(required = false, defaultValue = "false", value = "enableDummyCompetition") boolean enableDummyCompetition) {

		UniqueGameIdentifier toRet = gameStateController.createUniqueGameId();

		// translate 
		
		
		// save game
		gameStateController.createNewGame(toRet);
		
		return toRet;

	}

	@RequestMapping(value = "/{gameID}/players", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<UniquePlayerIdentifier> registerPlayer(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerRegistration playerRegistration) {
		
		UniquePlayerIdentifier newPlayerID = gameStateController.createUniquePlayerId();
		
		// validate if game id exists
		// validate if player id exists
		rules.forEach(rule -> rule.validateGameId(gameStateController.getGames(), gameID));
		rules.forEach(rule -> rule.validatePlayerReg(gameStateController.getGames(), newPlayerID, gameID));
		
		// translate 
		 
		
		// save player
		gameStateController.registerPlayer(newPlayerID, gameID, playerRegistration);
		
		ResponseEnvelope<UniquePlayerIdentifier> toRet = new ResponseEnvelope<>(newPlayerID);
		return toRet;

	}
	
	@RequestMapping(value = "/{gameID}/states/{playerID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<GameState> requestGameState(@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @PathVariable UniquePlayerIdentifier playerID) {
		
		
		
		// validate if game id exists
		// validate if player is in the respective game
		rules.forEach(rule -> rule.validateGameId(gameStateController.getGames(), gameID));
		rules.forEach(rule -> rule.validatePlayerId(gameStateController.getGames(), playerID, gameID));
				
		// translate 
		
		
		// process
		GameState newGameState = gameStateController.requestGameState(playerID, gameID, networkConverter);
		
		
		ResponseEnvelope<GameState> toRet = new ResponseEnvelope<>(newGameState);
		return toRet;

	}
	
	@RequestMapping(value = "/{gameID}/halfmaps", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope getHalfMap(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody HalfMap halfMap) {
		
		ResponseEnvelope toRet = new ResponseEnvelope<>();
		
		// validate if game exists
		// validate if player is in that game
		// validate if both players registered
		// validate if player's turn
		// validate map stuff
		
		rules.forEach(rule -> rule.validateGameId(gameStateController.getGames(), gameID));
		rules.forEach(rule -> rule.validatePlayerId(gameStateController.getGames(), new UniquePlayerIdentifier(halfMap.getUniquePlayerID()), gameID));
		if (gameStateController.getGames().get(gameID.getUniqueGameID()).getPlayerWithId(halfMap.getUniquePlayerID()).getHalfMap() != null) {
			throw new TooManyMapsSentException("Too many half maps sent", "Client " + halfMap.getUniquePlayerID() + " tried to send more than one half map");
		}
		rules.forEach(rule -> rule.validateGameState(gameStateController.getGames(), new UniquePlayerIdentifier(halfMap.getUniquePlayerID()), gameID));
		//rules.forEach(rule -> rule.myTurn(gameStateController.getGames(), new UniquePlayerIdentifier(halfMap.getUniquePlayerID()), gameID));
		if (!gameStateController.getGames().get(gameID.getUniqueGameID()).myTurn(halfMap.getUniquePlayerID())) {
			return new ResponseEnvelope<>();
		}
		rules.forEach(rule -> rule.validateHalfMap(halfMap));
		
		// translate 
		InternalHalfMap iHalfMap = networkConverter.convertHalfMapFrom(halfMap);
		
		// save 
		gameStateController.receiveHalfMap(iHalfMap, halfMap.getUniquePlayerID(), gameID.getUniqueGameID());
		gameStateController.swapPlayerOnTurn(gameID);
		gameStateController.updateGameStateId(gameID);
		
		return toRet;

	}
	
	@RequestMapping(value = "/{gameID}/moves", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope getMove(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerMove move) {
		
		ResponseEnvelope toRet = new ResponseEnvelope<>();
		
		// validate if game exists
		// validate if player is in that game
		// validate if player's turn
		// validate move
		rules.forEach(rule -> rule.validateGameId(gameStateController.getGames(), gameID));
		rules.forEach(rule -> rule.validatePlayerId(gameStateController.getGames(), new UniquePlayerIdentifier(move.getUniquePlayerID()), gameID));
		rules.forEach(rule -> rule.validateGameState(gameStateController.getGames(), new UniquePlayerIdentifier(move.getUniquePlayerID()), gameID));
		//rules.forEach(rule -> rule.myTurn(gameStateController.getGames(), new UniquePlayerIdentifier(move.getUniquePlayerID()), gameID));
		if (!gameStateController.getGames().get(gameID.getUniqueGameID()).myTurn(move.getUniquePlayerID())) {
			return new ResponseEnvelope<>();
		}
		rules.forEach(rule -> rule.validateMove(gameStateController.getGames(), move, gameID));
		
		// translate 
		
		
		// process
		gameStateController.receiveMove(move);
		gameStateController.swapPlayerOnTurn(gameID);
		gameStateController.updateGameStateId(gameID);
		
		return toRet;

	}


	@ExceptionHandler({ GenericExampleException.class })
	public @ResponseBody ResponseEnvelope<?> handleException(GenericExampleException ex, HttpServletResponse response) {
		ResponseEnvelope<?> result = new ResponseEnvelope<>(ex.getErrorName(), ex.getMessage());

		response.setStatus(HttpServletResponse.SC_OK);
		return result;
	}
}