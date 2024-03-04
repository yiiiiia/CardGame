package events;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case a card. The event returns the position in the player's hand the card
 * resides within.
 * <p>
 * { messageType = “cardClicked” position = <hand index position [1-6]> }
 *
 * @author Dr. Richard McCreadie
 */
public class CardClicked implements EventProcessor {

	/*
	 * This method is used to detect the placeable area near the tile, and then mark
	 * the color of the placeable area as highlight white
	 *
	 * @param tile:Tiles that need to be detected is:Which state to convert the tile
	 * to
	 */
	// Later put this method it in GameState

	/*
	 * This method is used to determine whether the coordinates are a placeable
	 * area.
	 *
	 * @param x:The abscissa of the tile y:vertical coordinate of tile
	 *
	 * @return:Return value of Boolean type, true means it can be placed, false
	 * means it cannot be placed.
	 */
	// Later put this method it in GameState

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		int handPosition = message.get("position").asInt();
		int pos = handPosition - 1;
		Player user = gameState.getUserPlayer();
		Card cardClicked = user.getHandCardByPos(pos);
		if (gameState.getActiveUnit() != null) {
			gameState.clearActiveUnit();
			gameState.redrawAllTiles(out);
		}
		if (cardClicked.getManacost() > user.getMana()) {
			BasicCommands.addPlayer1Notification(out, "Not enough mana", 5);
			return;
		}
		if (gameState.getActiveCard() != null) {
			if (cardClicked == gameState.getActiveCard()) {
				return;
			} else {
				gameState.clearActiveCard(out);
				gameState.redrawAllTiles(out);
			}
		}
		gameState.setActiveCard(out, cardClicked, handPosition);
		cardClicked.highlightTiles(out, gameState);
	}
}
