package events;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import structures.GameState;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case somewhere that is not on a card tile or the end-turn button.
 * 
 * { messageType = “otherClicked” }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor {
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		if (gameState.getActiveCard() != null) {
			gameState.redrawAllTiles(out);
			gameState.clearActiveCard(out);
		}
	}
}
