package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
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
		BasicCommands.drawTile(out, userTile, Tile.TILE_WHITE_MODE);
	}

	@Override
	public void castSpell(ActorRef out, GameState gameState, Tile tile) {
		if (manacost > gameState.getUserPlayer().getMana()) {
			BasicCommands.addPlayer1Notification(out, String.format("Not enough mana to use card %s", CARD_NAME), 5);
			return;
		}
		Unit unitOnTile = tile.getUnit();
		if (unitOnTile == null) {
			BasicCommands.addPlayer1Notification(out, String.format("Cannot use this card %s on empty tile", CARD_NAME),
					5);
			return;
		}
		if (unitOnTile != gameState.getUserAvatar()) {
			BasicCommands.addPlayer1Notification(out,
					String.format("Card %s can only be used on user avatar", CARD_NAME), 5);
			return;
		}
		gameState.deductManaFromPlayer(out, manacost, GameState.USER_MODE);
		unitOnTile.setShieldBuff(3);
		GameState.playEffectAnimation(out, StaticConfFiles.f1_buff, tile);
		setUsed(true); // mark card used
	}
}