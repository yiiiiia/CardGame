package structures.basic.card;

import java.util.List;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.StaticConfFiles;

public class BeamShockCard extends Card {

	public static final String CARD_NAME = "Beamshock";

	public void highlightTiles(ActorRef out, GameState gameState) {
		List<Unit> units = gameState.getAllUserUnits();
		for (Unit unit : units) {
			if (unit == gameState.getUserAvatar()) {
				// skip user avatar
				continue;
			}
			Tile tile = gameState.getUnitTile(unit);
			gameState.drawAndRecordHighlightedTile(out, tile, Tile.TILE_RED_MODE);
		}
	}

	@Override
	public void castSpell(ActorRef out, GameState gameState, Tile tile) {
		Unit unitOnTile = tile.getUnit();
		if (unitOnTile == null) {
			throw new IllegalStateException("Cannot use BeamShockCard on empty tile!");
		}
		if (gameState.isAiUnit(unitOnTile)) {
			throw new IllegalStateException("Cannot use BeamShockCard on ai unit!");
		}
		if (unitOnTile.isStunned()) {
			throw new IllegalStateException("Cannot use BeamShockCard on already stunned unit!");
		}
		gameState.deductManaFromPlayer(out, manacost, GameState.AI_MODE);
		GameState.playEffectAnimation(out, StaticConfFiles.f1_soulshatter, tile);
		GameState.playUnitAnimation(out, unitOnTile, UnitAnimationType.hit);
		unitOnTile.setStunned(true);
		GameState.playUnitAnimation(out, unitOnTile, UnitAnimationType.idle);
	}
}
