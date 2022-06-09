package server.gamestate;

import server.controllers.GameStateController;
import server.validation.FortRule;
import server.validation.GameIdRule;
import server.validation.HalfMapSizeRule;
import server.validation.MaxNoOfPlayersReachedRule;
import server.validation.MyTurnRule;
import server.validation.PlayerIdRule;
import server.validation.TerrainsNumberRule;

public class GameStateTests {
	GameIdRule gameIdRule = new GameIdRule();
	PlayerIdRule playerIdRule = new PlayerIdRule();
	MyTurnRule myTurnRule = new MyTurnRule();
	MaxNoOfPlayersReachedRule twoPlayersRegisteredRule = new MaxNoOfPlayersReachedRule();
	
	GameStateController controller = new GameStateController();
}
