package structures.basic.card;

import java.util.List;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.StaticConfFiles;

public class DarkTerminusCard extends Card {

	public static final String CARD_NAME = "Dark Terminus";

	@Override
	public void highlightTiles(ActorRef out, GameState gameState) {
		List<Unit> enemyUnits = gameState.getAllAIUnits();
		for (Unit enemy : enemyUnits) {
			if (enemy == gameState.getAiAvatar()) {
				// need to be excluded
				continue;
			}
			Tile tile = gameState.getUnitTile(enemy);
			gameState.drawAndRecordHighlightedTile(out, tile, Tile.TILE_RED_MODE);
		}
	}

	public String canCastSpellOnUnit(GameState gameState, Unit unit) {
		if (gameState.isUserUnit(unit) || unit == gameState.getAiAvatar()) {
			return "This card can only be used on AI unit that are summoned";
		}
		return "";
	}

	@Override
	public void castSpell(ActorRef out, GameState gameState, Tile tile) {
		Unit unit = tile.getUnit();
		if (unit == null) {
			throw new IllegalStateException("cannot use card ont empty tile: " + cardname);
		}
		if (gameState.isUserUnit(unit)) {
			throw new IllegalStateException("cannot use card ont user unit: " + cardname);
		}
		if (unit == gameState.getAiAvatar()) {
			throw new IllegalStateException("cannot use card ont ai avatar: " + cardname);
		}
		gameState.deductManaFromPlayer(out, manacost, GameState.USER_MODE);
		GameState.playEffectAnimation(out, StaticConfFiles.f1_inmolation, tile);
		gameState.dealDamangeToUnit(out, unit, unit.getHealth());
		BasicCommands.sleep(500);
		// summon wraithling on the tile
		gameState.summonWraithling(out, tile, GameState.USER_MODE);
	}
}
