package structures.basic.card;

import java.util.List;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;

public class SundropElixirCard extends Card {

	public static final String CARD_NAME = "Sundrop Elixir";

	public void highlightTiles(ActorRef out, GameState gameState) {
		List<Unit> units = gameState.getAiUnits();
		// highlight the allied units
		for (Unit unit : units) {
			Tile tile = gameState.getUnitTile(unit);
			BasicCommands.drawTile(out, tile, Tile.TILE_WHITE_MODE);
		}
	}

	@Override
	public void castSpell(ActorRef out, GameState gameState, Tile tile) {
		Unit unitOnTile = tile.getUnit();
		if (unitOnTile == null) {
			BasicCommands.addPlayer1Notification(out, String.format("Cannot use card %s on empty tile", CARD_NAME), 5);
			return;
		}
		if (!gameState.isAiUnit(unitOnTile)) {
			BasicCommands.addPlayer1Notification(out,
					String.format("Card %s can only be used on ally units", CARD_NAME), 5);
			return;
		}
		gameState.deductManaFromPlayer(out, manacost, GameState.AI_MODE);
		unitOnTile.healSelf(4);
		BasicCommands.setUnitHealth(out, unitOnTile, unitOnTile.getHealth());
	}
}
