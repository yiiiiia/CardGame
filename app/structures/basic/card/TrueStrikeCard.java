package structures.basic.card;

import java.util.List;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.StaticConfFiles;

public class TrueStrikeCard extends Card {

	public static final String CARD_NAME = "Truestrike";

	public void highlightTiles(ActorRef out, GameState gameState) {
		List<Unit> units = gameState.getAllUserUnits();
		for (Unit unit : units) {
			Tile tile = gameState.getUnitTile(unit);
			gameState.drawAndRecordHighlightedTile(out, tile, Tile.TILE_RED_MODE);
		}
	}

	@Override
	public void castSpell(ActorRef out, GameState gameState, Tile tile) {
		Unit unitOnTile = tile.getUnit();
		if (unitOnTile == null) {
			throw new IllegalStateException("Cannot use TrueStrikeCard on empty tile!");
		}
		if (gameState.isAiUnit(unitOnTile)) {
			throw new IllegalStateException("Cannot use TrueStrikeCard on ai unit!");
		}
		gameState.deductManaFromPlayer(out, manacost, GameState.AI_MODE);
		GameState.playEffectAnimation(out, StaticConfFiles.f1_inmolation, tile);
		BasicCommands.sleep(250);
		gameState.dealDamangeToUnit(out, unitOnTile, 2);
	}
}
