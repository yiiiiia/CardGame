package structures.basic.card;

import java.util.List;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.unit.ShadowWatcher;

public class ShadowWatcherCard extends Card {

	public static final String CARD_NAME = "Shadow Watcher";

	@Override
	public void highlightTiles(ActorRef out, GameState gameState) {
		List<Tile> tiles = gameState.getTilesForSummon(GameState.USER_MODE);
		for (Tile tile : tiles) {
			BasicCommands.drawTile(out, tile, Tile.TILE_WHITE_MODE);
		}
	}

	@Override
	public Class<? extends Unit> getSummonedCreatureClass() {
		return ShadowWatcher.class;
	}
}
