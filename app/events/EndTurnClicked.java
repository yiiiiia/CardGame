package events;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Player;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case the end-turn button.
 * 
 * { messageType = “endTurnClicked” }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor {
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		gameState.clearActiveCard(out);
		gameState.clearActiveUnit();
		gameState.clearHighlightedTiles(out);
		BasicCommands.addPlayer1Notification(out, "Start AI mode", 3);
		Player aiPlayer = gameState.getAiPlayer();
		aiPlayer.refreshManaByTurnNum(gameState.getTurn());
		BasicCommands.setPlayer2Mana(out, aiPlayer);
		gameState.setGameMode(GameState.AI_MODE);
		gameState.getUserPlayer().resetStatus(out, gameState);
		gameState.getAiPlayer().playAI(out, gameState);
	}
}
