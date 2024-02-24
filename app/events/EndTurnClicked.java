package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;

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
		gameState.setIgnoreEvent(true); //from GameState class

		//clearMana
		gameState.getUserPlayer().setMana(0); //from Player class
		BasicCommands.setPlayer1Mana(out, gameState.getUserPlayer());
		
		//drawCard
		Random random = new Random();
		int r = random.nextInt(gameState.getUserPlayer().getCardRemain().size());
		
		gameState.getUserPlayer().addHandCard(gameState.getUserPlayer().getCardRemain().get(r)); //from Player class
		
		//setAIAsActivePlayer
		gameState.setPlayerMode(1);
		//gameState.setAIAsActivePlayer(); 
	}
}
