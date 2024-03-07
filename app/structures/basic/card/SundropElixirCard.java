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
		List<Unit> units = gameState.getAllAIUnits();
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
			throw new IllegalStateException("can not use SundropElixirCard on empty tile");
		}
		if (!gameState.isAiUnit(unitOnTile)) {
			throw new IllegalStateException("can only use SundropElixirCard on AI unit");
		}
		gameState.deductManaFromPlayer(out, manacost, GameState.AI_MODE);
		unitOnTile.healSelf(4);
		BasicCommands.setUnitHealth(out, unitOnTile, unitOnTile.getHealth());
		setUsed(true);
	}
}
