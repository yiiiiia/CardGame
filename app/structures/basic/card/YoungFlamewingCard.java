package structures.basic.card;

import java.util.List;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.unit.YoungFlamewing;

public class YoungFlamewingCard extends Card {

	public static final String CARD_NAME = "Young Flamewing";

	@Override
	public void highlightTiles(ActorRef out, GameState gameState) {
		List<Tile> tiles = gameState.getTilesForSummon(GameState.AI_MODE);
		for (Tile tile : tiles) {
			BasicCommands.drawTile(out, tile, Tile.TILE_WHITE_MODE);
		}
	}

	@Override
	public Class<? extends Unit> getSummonedCreatureClass() {
		return YoungFlamewing.class;
	}
}
