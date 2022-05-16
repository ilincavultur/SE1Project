package client.main;


import client.controllers.GameStateController;


public class MainClient {

	public static void main(String[] args) {

		/*
		 * IMPORTANT: Parsing/Handling of starting parameters.
		 * 
		 * args[0] = Game Mode, you Can use this to know that your code is running on
		 * the evaluation server (if this is the case args[0] = TR). If this is the
		 * case, only a command line interface must be displayed. Also, no JavaFX and
		 * Swing UI components and classes must be used/executed by your Client in any
		 * way IF args[0] = TR.
		 * 
		 * args[1] = Server URL, will hold the server URL your Client should use. Note,
		 * only use the server URL supplied here as the URL used by you during the
		 * development and by the evaluation server (for grading) is NOT the same!
		 * args[1] enables your Client always to get the correct one.
		 * 
		 * args[2] = Holds the game ID which your Client should use. For testing
		 * purposes, you can create a new one by accessing
		 * http://swe1.wst.univie.ac.at:18235/games with your web browser. IMPORTANT: If
		 * a value is stored in args[2], you MUST use it! DO NOT create new games in
		 * your code in such a case!
		 * 
		 * DON'T FORGET TO EVALUATE YOUR FINAL IMPLEMENTATION WITH OUR TEST SERVER. THIS
		 * IS ALSO THE BASE FOR GRADING. THE TEST SERVER CAN BE FOUND AT:
		 * http://swe1.wst.univie.ac.at/
		 * 
		 * HINT: The assignment section in Moodle also explains all the important
		 * aspects about the start parameters/arguments. Use the Run Configurations (as
		 * shown during the first lecture) in Eclipse to simulate the starting of an
		 * application with start parameters or implement your argument parsing code to
		 * become more flexible (e.g., to mix hardcoded and supplied parameters whenever
		 * the one or the other is available).
		 */

		// parse the parameters, otherwise the automatic evaluation will not work on
		// http://swe1.wst.univie.ac.at
		//String serverBaseUrl = args[1];
		//String gameId = args[2];
		String serverBaseUrl = "http://swe1.wst.univie.ac.at";
		String gameId = "WYbJt";
		
		
		GameStateController gameController = new GameStateController(gameId, serverBaseUrl);
		
		gameController.startGame();

		
	}

	
}
