package events;

import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * In the user’s browser, the game is running in an infinite loop, where there is around a 1 second delay 
 * between each loop. Its during each loop that the UI acts on the commands that have been sent to it. A 
 * heartbeat event is fired at the end of each loop iteration. As with all events this is received by the Game 
 * Actor, which you can use to trigger game logic.
 * 
 * { 
 *   String messageType = “heartbeat”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Heartbeat implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		//GameEnd
		String gameEndText;

		if(gameState.checkEndCondition()) {

			if(gameState.getUserPlayer().getHealth() <= 0) {
				gameEndText = "You win";
			}else {
				gameEndText = "You lose";
			}
			BasicCommands.addPlayer1Notification(out, gameEndText, 2);
			gameState.gameInitalised = false;
		}

	}

}
