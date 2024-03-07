package events;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.AiPlayer;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.unit.AIAvatar;
import structures.basic.unit.PlayerAvatar;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * <p>
 * { messageType = “initalize” }
 *
 * @author Dr. Richard McCreadie
 */
public class Initalize implements EventProcessor {
	public static final int INITIAL_HEALTH = 20;
	public static final int INITIAL_MANA = 2;
	public static final int INITIAL_ATTACK = 2;

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// Turn Initialization
		gameState.setTurn(1);

		// PlayerMode Initialization
		gameState.setGameMode(GameState.USER_MODE);

		// Players Initialization
		playersInitialization(out, gameState);

		// Board Initialization
		boardInitialization(out, gameState);

		// Avatars Initialization
		avatarsInitialization(out, gameState);

		// Deck Initialization
		deckInitialization(gameState);

		// Cards Initialization
		cardsInitialization(out, gameState);
	}

	private void playersInitialization(ActorRef out, GameState gameState) {
		Player user = new Player(INITIAL_HEALTH, INITIAL_MANA);
		AiPlayer ai = new AiPlayer(INITIAL_HEALTH, INITIAL_MANA);
		gameState.setUserPlayer(user);
		gameState.setAiPlayer(ai);
		BasicCommands.setPlayer1Mana(out, user);
		BasicCommands.setPlayer1Health(out, user);
		BasicCommands.setPlayer2Health(out, ai);
		BasicCommands.setPlayer2Mana(out, ai);
	}

	private void boardInitialization(ActorRef out, GameState gameState) {
		gameState.initGameTiles();
		gameState.redrawAllTiles(out);
	}

	private void avatarsInitialization(ActorRef out, GameState gameState) {
		// create human avatar
		Unit userAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, GameState.genUnitId(),
				PlayerAvatar.class);
		gameState.setUserAvatar(userAvatar);
		// place human avatar
		Tile userTile = gameState.getTileByPos(1, 2);
		gameState.getUserPlayer().putUnitOnTile(userTile, userAvatar);
		BasicCommands.drawUnit(out, userAvatar, userTile);
		BasicCommands.sleep(200);

		// create ai avatar
		Unit aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, GameState.genUnitId(), AIAvatar.class);
		gameState.setAiAvatar(aiAvatar);
		// place ai avatar
		Tile aiTile = gameState.getTileByPos(7, 2);
		gameState.getAiPlayer().putUnitOnTile(aiTile, aiAvatar);
		BasicCommands.drawUnit(out, aiAvatar, aiTile);
		BasicCommands.sleep(200);

		BasicCommands.setUnitHealth(out, userAvatar, INITIAL_HEALTH);
		BasicCommands.setUnitHealth(out, aiAvatar, INITIAL_HEALTH);
		BasicCommands.setUnitAttack(out, userAvatar, INITIAL_ATTACK);
		BasicCommands.setUnitAttack(out, aiAvatar, INITIAL_ATTACK);
	}

	private void deckInitialization(GameState gameState) {
		gameState.getUserPlayer().setDeckCards(OrderedCardLoader.getPlayer1Cards(2));
		gameState.getAiPlayer().setDeckCards(OrderedCardLoader.getPlayer2Cards(2));
	}

	private void cardsInitialization(ActorRef out, GameState gameState) {
		// draw three cards for human, and show them
		Player user = gameState.getUserPlayer();
		for (int i = 0; i < 3; i++) {
			int n = GameState.nextRandInt(user.getDeckCards().size());
			Card card = user.getDeckCards().remove(n);
			user.putCardAtPos(card, i);
			BasicCommands.drawCard(out, card, i + 1, Card.CARD_NORMAL_MODE);
		}
		// draw three cards for ai
		Player ai = gameState.getAiPlayer();
		for (int i = 0; i < 3; i++) {
			int n = GameState.nextRandInt(ai.getDeckCards().size());
			Card card = ai.getDeckCards().remove(n);
			ai.putCardAtPos(card, i);
		}
	}
}
