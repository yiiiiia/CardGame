package events;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Unit;

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
		// clearMana
		Player user = gameState.getUserPlayer();
		user.setMana(0); // from Player class
		BasicCommands.setPlayer1Mana(out, user);
		// clear stun status
		clearUserUnitStatus(gameState);
		// clear hand cards
		clearHandCards(out);
		// draw a new card
		user.drawOneNewCard();
		// display new cards
		for (int i = 0; i < user.getHandCards().size(); i++) {
			Card card = user.getHandCardByPos(i);
			BasicCommands.drawCard(out, card, i + 1, Card.CARD_NORMAL_MODE);
		}
		gameState.setGameMode(GameState.AI_MODE);
		gameState.getAiPlayer().playAiLogic(out, gameState);
	}

	private void clearUserUnitStatus(GameState gameState) {
		for (Unit u : gameState.getUserUnits()) {
			u.setStunned(false);
			u.setHasMoved(false);
			u.setHasAttacked(false);
		}
	}

	private void clearHandCards(ActorRef out) {
		for (int i = 0; i < Player.MAX_HAND_CARD_NUM; ++i) {
			BasicCommands.deleteCard(out, i + 1);
		}
	}
}
