package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import java.util.*;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * 
 * { 
 *   messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		//ignorePlayerAction
		gameState.setIgnoreEvent(true);
		//clearMana
		gameState.getAllPlayer[0].setMana(0);
		//drawCard
		Random random = new Random(gameState.getAllPlayer[0].getcardsRemain().size());
		List<Card> cardRemain = gameState.getAllPlayer[0].getCardsRemain();
		if(cardRemain < 6) {
			BasicCommands.drawCard(out, cardRemain.get(random.nextInt(cardRemain.size())), cardRemain.size() + 1, 0);
		}
		//setAIAsActivePlayer
		gameState.setAIAsActivePlayer();

	}

}
