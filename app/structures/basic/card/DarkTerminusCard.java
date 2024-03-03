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
		List<Unit> enemyUnits = gameState.getAiUnits();
		for (Unit enemy : enemyUnits) {
			if (enemy == gameState.getAiAvatar()) {
				// need to be excluded
				continue;
			}
			Tile tile = gameState.getUnitTile(enemy);
			BasicCommands.drawTile(out, tile, Tile.TILE_RED_MODE);
		}
	}

	@Override
	public void castSpell(ActorRef out, GameState gameState, Tile tile) {
		Unit enemy = tile.getUnit();
		if (enemy == null || enemy == gameState.getAiAvatar()) {
			BasicCommands.addPlayer1Notification(out, "Can only use this card on enemy creature!", 5);
			return;
		}
		gameState.deductManaFromPlayer(out, manacost, GameState.USER_MODE);
		GameState.playEffectAnimation(out, StaticConfFiles.f1_inmolation, tile);
		gameState.dealDamangeToUnit(out, enemy, enemy.getHealth());
		BasicCommands.sleep(500);
		// summon wraithling on the tile
		gameState.summonWraithling(out, tile, GameState.USER_MODE);
	}
}
