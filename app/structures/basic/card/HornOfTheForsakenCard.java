package structures.basic.card;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.StaticConfFiles;

public class HornOfTheForsakenCard extends Card {

	public static final String CARD_NAME = "Horn of the Forsaken";

	@Override
	public void highlightTiles(ActorRef out, GameState gameState) {
		Unit userAvatar = gameState.getUserAvatar();
		Tile userTile = gameState.getUnitTile(userAvatar);
		gameState.drawAndRecordHighlightedTile(out, userTile, Tile.TILE_WHITE_MODE);
	}

	public String canCastSpellOnUnit(GameState gameState, Unit unit) {
		if (unit.getShieldBuff() > 0) {
			return "The card effect is currently in use";
		}
		if (unit != gameState.getUserAvatar()) {
			return "This card can only be used on user avatar";
		}
		return "";
	}

	@Override
	public void castSpell(ActorRef out, GameState gameState, Tile tile) {
		if (manacost > gameState.getUserPlayer().getMana()) {
			throw new IllegalStateException("not enough mana to use card: " + this);
		}
		if (tile.getUnit() == null) {
			throw new IllegalStateException("cannot use this card on empty tile: " + this);
		}
		if (tile.getUnit().getShieldBuff() > 0) {
			throw new IllegalStateException("the card effect is currently in use" + this);
		}
		if (tile.getUnit() != gameState.getUserAvatar()) {
			throw new IllegalStateException("this card can only be used on user avatar" + this);
		}
		gameState.deductManaFromPlayer(out, manacost, GameState.USER_MODE);
		GameState.playEffectAnimation(out, StaticConfFiles.f1_buff, tile);
		tile.getUnit().setShieldBuff(3);
	}
}