package server.main;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import server.exceptions.NotPlayersTurnException;
import server.exceptions.PlayerIdException;
import server.exceptions.TooManyMapsSentException;
import server.exceptions.TooManyPlayersException;
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
import server.validation.OnlyOneMapPerPlayerRule;
import server.validation.TerrainsNumberRule;
import server.validation.WaterOnEdgesRule;

@RestController
@RequestMapping(value = "/games")
public class ServerEndpoints {
	
	private GameStateController gameStateController = new GameStateController();
	private NetworkConverter networkConverter = new NetworkConverter();
	private static final Logger logger = LoggerFactory.getLogger(ServerEndpoints.class);
	private static List<IRuleValidation> rules = List.of(	new BothPlayersRegisteredRule(), 
											new GameIdRule(), new DontMoveIntoWaterRule(), 
											new DontMoveOutsideMapRule(), new FieldsCoordinatesRule(), 
											new FortRule(), new HalfMapSizeRule(), 
											new MaxNoOfPlayersReachedRule(), new MyTurnRule(), 
											new NoIslandsRule(), new PlayerIdRule(), 
											new TerrainsNumberRule(), new WaterOnEdgesRule(), 
											new OnlyOneMapPerPlayerRule()
										);


	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody UniqueGameIdentifier newGame(
			@RequestParam(required = false, defaultValue = "false", value = "enableDebugMode") boolean enableDebugMode,
			@RequestParam(required = false, defaultValue = "false", value = "enableDummyCompetition") boolean enableDummyCompetition) {

		UniqueGameIdentifier toReturn = gameStateController.createUniqueGameId();

		gameStateController.createNewGame(toReturn);
		
		logger.info("New Game Created");
		
		return toReturn;

	}

	@RequestMapping(value = "/{gameID}/players", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<UniquePlayerIdentifier> registerPlayer(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerRegistration playerRegistration) {
		
		UniquePlayerIdentifier newPlayerID = gameStateController.createUniquePlayerId();
		
		try {
			rules.forEach(rule -> rule.validateGameId(gameStateController.getGames(), gameID));
			rules.forEach(rule -> rule.validatePlayerReg(gameStateController.getGames(), newPlayerID, gameID));
		} catch (GameIdException gameIdException) {
			logger.info("game Id not found");
			throw gameIdException;
		} catch (TooManyPlayersException tooManyPlayersException) {
			logger.info("max number of players reached, cannot register");
			throw tooManyPlayersException;
		}

		gameStateController.registerPlayer(newPlayerID, gameID, playerRegistration);
		
		ResponseEnvelope<UniquePlayerIdentifier> toReturn = new ResponseEnvelope<>(newPlayerID);
		
		logger.info("New Player Registered");
		
		return toReturn;

	}
	
	@RequestMapping(value = "/{gameID}/halfmaps", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope getHalfMap(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody HalfMap halfMap) {
		
		ResponseEnvelope toReturn = new ResponseEnvelope<>();
		
		try {
			rules.forEach(rule -> rule.validateGameId(gameStateController.getGames(), gameID));	
			rules.forEach(rule -> rule.validatePlayerId(gameStateController.getGames(), new UniquePlayerIdentifier(halfMap.getUniquePlayerID()), gameID));
			rules.forEach(rule -> rule.validateGameState(gameStateController, new UniquePlayerIdentifier(halfMap.getUniquePlayerID()), gameID));
			rules.forEach(rule -> rule.validateHalfMap(halfMap));
			rules.forEach(rule -> rule.myTurn(gameStateController.getGames(), new UniquePlayerIdentifier(halfMap.getUniquePlayerID()), gameID));
		} catch (GameIdException e) {
			logger.info("game Id not found");
			throw e;
		} catch (PlayerIdException e) {
			logger.info("user Id not found");
			throw e;
		} catch (NotEnoughPlayersException e) {
			logger.info("2 players must be registered, only one found");
			throw e;
		} catch (TooManyMapsSentException e) {
			logger.info("Half Map was already sent, cannot send another one");
			throw e;
		} catch(HalfMapException e) {
			logger.info("Half Map was incorrect");
			gameStateController.getGames().get(gameID.getUniqueGameID()).setWinner(halfMap.getUniquePlayerID());
			throw e;
		} catch (NotPlayersTurnException e) {
			logger.info("Player sent halfmap when it wasn't his turn");
			gameStateController.getGames().get(gameID.getUniqueGameID()).setWinner(halfMap.getUniquePlayerID());
			throw e;
		}
	
		InternalHalfMap iHalfMap = networkConverter.convertHalfMapFrom(halfMap);
	
		gameStateController.receiveHalfMap(iHalfMap, halfMap.getUniquePlayerID(), gameID.getUniqueGameID());
		gameStateController.swapPlayerOnTurn(gameID);
		gameStateController.updateGameStateId(gameID);
		
		logger.info("Half Map has been received and saved");
		
		return toReturn;

	}
	
	@RequestMapping(value = "/{gameID}/states/{playerID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<GameState> requestGameState(@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @PathVariable UniquePlayerIdentifier playerID) {
		
		try {
			rules.forEach(rule -> rule.validateGameId(gameStateController.getGames(), gameID));
			rules.forEach(rule -> rule.validatePlayerId(gameStateController.getGames(), playerID, gameID));
		} catch (GameIdException e) {
			logger.info("game Id not found");
			throw e;
		} catch (PlayerIdException e) {
			logger.info("user Id not found");
			throw e;
		}
				
		if (gameStateController.bothHalfMapsPresent(gameID)) {
			gameStateController.assembleHalfMaps(gameID);
		}

		GameState newGameState = gameStateController.requestGameState(playerID, gameID, networkConverter);
		
		ResponseEnvelope<GameState> toRet = new ResponseEnvelope<>(newGameState);
		
		logger.info("Game State sent");
		
		return toRet;

	}
	
	@RequestMapping(value = "/{gameID}/moves", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope getMove(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerMove move) {
		
		ResponseEnvelope toReturn = new ResponseEnvelope<>();
	
		try {
			rules.forEach(rule -> rule.validateGameId(gameStateController.getGames(), gameID));	
			rules.forEach(rule -> rule.validatePlayerId(gameStateController.getGames(), new UniquePlayerIdentifier(move.getUniquePlayerID()), gameID));
			rules.forEach(rule -> rule.validateMove(gameStateController.getGames(), move, gameID));
			rules.forEach(rule -> rule.myTurn(gameStateController.getGames(), new UniquePlayerIdentifier(move.getUniquePlayerID()), gameID));
		} catch (GameIdException e) {
			logger.info("game Id not found");
			throw e;
		} catch (PlayerIdException e) {
			logger.info("user Id not found");
			throw e;
		} catch (MoveException e) {
			logger.info("Move sent was wrong");
			gameStateController.getGames().get(gameID.getUniqueGameID()).setWinner(move.getUniquePlayerID());
			throw e;
		} catch (NotPlayersTurnException e) {
			logger.info("Player sent move when it wasn't his turn");
			gameStateController.getGames().get(gameID.getUniqueGameID()).setWinner(move.getUniquePlayerID());
			throw e;
		}

		gameStateController.receiveMove(gameID, move, networkConverter);
		gameStateController.swapPlayerOnTurn(gameID);
		gameStateController.updateGameStateId(gameID);
		
		logger.info("Move received and processed");
		
		return toReturn;

	}


	@ExceptionHandler({ GenericExampleException.class })
	public @ResponseBody ResponseEnvelope<?> handleException(GenericExampleException ex, HttpServletResponse response) {
		ResponseEnvelope<?> result = new ResponseEnvelope<>(ex.getErrorName(), ex.getMessage());

		response.setStatus(HttpServletResponse.SC_OK);
		return result;
	}
}
