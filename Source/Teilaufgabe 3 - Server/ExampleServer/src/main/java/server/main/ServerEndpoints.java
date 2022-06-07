package server.main;

import java.util.List;

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
import server.exceptions.GameIdException;
import server.exceptions.GenericExampleException;
import server.exceptions.HalfMapException;
import server.exceptions.MoveException;
import server.exceptions.NotEnoughPlayersException;
import server.exceptions.PlayerIdException;
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

		// create unique game id
		UniqueGameIdentifier toRet = gameStateController.createUniqueGameId();

		// translate + save game
		gameStateController.createNewGame(toRet);
		
		return toRet;

	}

	@RequestMapping(value = "/{gameID}/players", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<UniquePlayerIdentifier> registerPlayer(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerRegistration playerRegistration) {
		
		// create unique player id
		UniquePlayerIdentifier newPlayerID = gameStateController.createUniquePlayerId();
		
		// validate if game id exists
		rules.forEach(rule -> rule.validateGameId(gameStateController.getGames(), gameID));
		// validate if max number of players not already registered
		rules.forEach(rule -> rule.validatePlayerReg(gameStateController.getGames(), newPlayerID, gameID));
	
		// translate + save player
		gameStateController.registerPlayer(newPlayerID, gameID, playerRegistration);
		
		ResponseEnvelope<UniquePlayerIdentifier> toRet = new ResponseEnvelope<>(newPlayerID);
		
		return toRet;

	}
	
	@RequestMapping(value = "/{gameID}/states/{playerID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<GameState> requestGameState(@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @PathVariable UniquePlayerIdentifier playerID) {
		
		// validate if game id exists
		rules.forEach(rule -> rule.validateGameId(gameStateController.getGames(), gameID));
		// validate if player is in the respective game
		rules.forEach(rule -> rule.validatePlayerId(gameStateController.getGames(), playerID, gameID));
				
		// if both maps available, create full map
		if (gameStateController.bothHalfMapsPresent(gameID)) {
			gameStateController.assembleHalfMaps(gameID);
		}
		
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
		try {
			rules.forEach(rule -> rule.validateGameId(gameStateController.getGames(), gameID));	
		} catch (GameIdException e) {
			throw e;
		}
		
		try {
			rules.forEach(rule -> rule.validatePlayerId(gameStateController.getGames(), new UniquePlayerIdentifier(halfMap.getUniquePlayerID()), gameID));	
		} catch (PlayerIdException e) {
			throw e;
		}
		
		/*try {
			rules.forEach(rule -> rule.validateGameState(gameStateController.getGames(), new UniquePlayerIdentifier(halfMap.getUniquePlayerID()), gameID));	
		} catch (NotEnoughPlayersException e) {
			throw e;
		}*/
		
		if (gameStateController.bothPlayersRegistered(gameID) == false) {
			gameStateController.getGames().get(gameID.getUniqueGameID()).setWinner(halfMap.getUniquePlayerID());
			throw new NotEnoughPlayersException("Only one client has registered", "Client tried to send half Map but not both players were registered");
		}

		if (gameStateController.getGames().get(gameID.getUniqueGameID()).getPlayerWithId(halfMap.getUniquePlayerID()).getHalfMap() != null) {
			gameStateController.getGames().get(gameID.getUniqueGameID()).setWinner(halfMap.getUniquePlayerID());
			throw new TooManyMapsSentException("Too many half maps sent", "Client " + halfMap.getUniquePlayerID() + " tried to send more than one half map");
		}
		
		if (!gameStateController.getGames().get(gameID.getUniqueGameID()).myTurn(halfMap.getUniquePlayerID())) {
			gameStateController.getGames().get(gameID.getUniqueGameID()).setWinner(halfMap.getUniquePlayerID());
			return new ResponseEnvelope<>();
		}

		try {
			rules.forEach(rule -> rule.validateHalfMap(halfMap));	
		} catch(HalfMapException e) {
			gameStateController.getGames().get(gameID.getUniqueGameID()).setWinner(halfMap.getUniquePlayerID());
			throw e;
		}
		
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
		//rules.forEach(rule -> rule.validateGameId(gameStateController.getGames(), gameID));
		//rules.forEach(rule -> rule.validatePlayerId(gameStateController.getGames(), new UniquePlayerIdentifier(move.getUniquePlayerID()), gameID));
		try {
			rules.forEach(rule -> rule.validateGameId(gameStateController.getGames(), gameID));	
		} catch (GameIdException e) {
			throw e;
		}
		
		try {
			rules.forEach(rule -> rule.validatePlayerId(gameStateController.getGames(), new UniquePlayerIdentifier(move.getUniquePlayerID()), gameID));	
		} catch (PlayerIdException e) {
			throw e;
		}
		
		/*if (gameStateController.bothPlayersRegistered(gameID) == false) {
			gameStateController.getGames().get(gameID.getUniqueGameID()).setWinner(move.getUniquePlayerID());
			throw new NotEnoughPlayersException("Only one client has registered", "Client tried to send half Map but not both players were registered");
		}*/
		
		//rules.forEach(rule -> rule.validateGameState(gameStateController.getGames(), new UniquePlayerIdentifier(move.getUniquePlayerID()), gameID));
		
		if (!gameStateController.getGames().get(gameID.getUniqueGameID()).myTurn(move.getUniquePlayerID())) {
			return new ResponseEnvelope<>();
		}
		
		try {
			rules.forEach(rule -> rule.validateMove(gameStateController.getGames(), move, gameID));	
		} catch (MoveException e) {
			gameStateController.getGames().get(gameID.getUniqueGameID()).setWinner(move.getUniquePlayerID());
			throw e;
		} 

		// translate + process
		gameStateController.receiveMove(gameID, move, networkConverter);
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
