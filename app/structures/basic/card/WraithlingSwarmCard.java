package structures.basic.card;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import actors.GameActor;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;

public class WraithlingSwarmCard extends Card {

	public static final String CARD_NAME = "Wraithling Swarm";

	private int remainingWraithling;

	public WraithlingSwarmCard() {
		super();
		remainingWraithling = 3;
	}

	@Override
	public void highlightTiles(ActorRef out, GameState gameState) {
		List<Tile> gameTiles = gameState.getGameTiles();
		for (Tile tile : gameTiles) {
			if (!tile.isOccupied()) {
				gameState.drawAndRecordHighlightedTile(out, tile, Tile.TILE_WHITE_MODE);
			}
		}
		String msg = String.format("Select a tile to summon Wraithlings, remaining: %d", remainingWraithling);
		BasicCommands.addPlayer1Notification(out, msg, 5);
	}

	@Override
	public void castSpell(ActorRef out, GameState gameState, Tile tile) {
		if (remainingWraithling == 0) {
			throw new IllegalStateException("Cannot summon more Wraithlings");
		}
		if (tile.isOccupied()) {
			BasicCommands.addPlayer1Notification(out, "Cannot summon Wraithlings on occupied tile!", 5);
			return;
		}
		gameState.clearHighlightedTiles(out);
		gameState.summonWraithling(out, tile, GameState.USER_MODE);
		remainingWraithling--;
		if (gameState.getDelegatedCard() == null) {
			gameState.setDelegatedCard(this);
			gameState.deductManaFromPlayer(out, manacost, GameState.USER_MODE);
		}
		if (remainingWraithling > 0) {
			highlightTiles(out, gameState);
		} else {
			gameState.clearActiveCard(out);
			gameState.deleteUserCard(out, this);
			gameState.clearDelegateCard();
		}
	}

	@Override
	public void delegateEventProcess(ActorRef out, GameState gameState, String messageType, JsonNode message) {
		if (remainingWraithling == 0) {
			throw new IllegalStateException("Cannot summon more Wraithlings");
		}
		switch (messageType) {
		case GameActor.TILE_CLICK_EVENT:
			int tilex = message.get("tilex").asInt();
			int tiley = message.get("tiley").asInt();
			Tile tile = gameState.getTileByPos(tilex, tiley);
			castSpell(out, gameState, tile);
			break;
		default:
			highlightTiles(out, gameState);
		}
	}
}