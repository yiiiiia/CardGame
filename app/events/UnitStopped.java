package events;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import structures.Action;
import structures.GameState;

/**
 * Indicates that a unit instance has stopped moving. The event reports the
 * unique id of the unit.
 * 
 * { messageType = “unitStopped” id = <unit id> }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class UnitStopped implements EventProcessor {
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		if (gameState.getPendingAction() != null) {
			Action action = gameState.getPendingAction();
			action.doAction(out, gameState);
			// clear this pending action
			gameState.setPendingAction(null);
		}
		gameState.setHasMovingUnit(false);
	}
}