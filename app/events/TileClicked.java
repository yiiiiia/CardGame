package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Action;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile
 * that was
 * clicked. Tile indices start at 1.
 * 
 * {
 * messageType = “tileClicked”
 * tilex = <x index of the tile>
 * tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor {
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		Tile tile = gameState.getTileByPos(tilex, tiley);
		if (tile == null) {
			throw new RuntimeException(String.format("tile not exists, tileX: %d, tileY: %d", tilex, tiley));
		}
		Unit userUnit = gameState.getUserPlayer().getUnitByTile(tile);
		Unit aiUnit = gameState.getAiPlayer().getUnitByTile(tile);
		if (userUnit != null) {
			// The tile clicked contains a user unit
			handleUserUnitOnTile(out, gameState, tile, userUnit);
			return;
		}
		if (aiUnit != null) {
			// The tile clicked contains an AI unit
			handleAiUnitOnTile(out, gameState, tile, aiUnit);
			return;
		}
		// There's no unit on the tile
		handleNoUnitOnTile(out, gameState, tile);
	}

	public void handleUserUnitOnTile(ActorRef out, GameState gameState, Tile tileClicked, Unit userUnit) {
		if (gameState.getActivateCard() != null) {
			// Try to use the active card on the user unit
			tryApplyCardOnUnit(out, gameState, gameState.getActivateCard(), userUnit);
			return;
		}
		if (gameState.getActiveUnit() != null) {
			// There's already an active unit
			// If the active unit is the same as the user unit clicked, do nothing
			if (gameState.getActiveUnit() == userUnit) {
				return;
			}
		}
		// clear the existing highlighted tiles (if any)
		redrawGameTiles(out, gameState);
		gameState.setActiveUnit(userUnit);
		// highlight tiles if the unit is allowd to move
		if (gameState.unitCanMove(userUnit)) {
			List<Tile> tilesAccessible = gameState.getTilesUnitCanMoveTo(userUnit);
			for (Tile tile : tilesAccessible) {
				BasicCommands.drawTile(out, tile, Tile.TILE_WHITE_MODE);
			}
		}
		// highlight tiles if the unit is able to attack
		if (gameState.unitCanAttack(userUnit, null)) {
			List<Tile> enemyTiles = gameState.getTilesWithEnemyUnitsInRange(userUnit, GameState.HUMAN_MODE);
			for (Tile tile : enemyTiles) {
				BasicCommands.drawTile(out, tile, Tile.TILE_RED_MODE);
			}
		}
	}

	public void handleAiUnitOnTile(ActorRef out, GameState gameState, Tile tileClicked, Unit aiUnit) {
		if (gameState.getActivateCard() != null) {
			tryApplyCardOnUnit(out, gameState, gameState.getActivateCard(), aiUnit);
			return;
		}
		if (gameState.getActiveUnit() != null) {
			Unit userUnit = gameState.getActiveUnit();
			if (!gameState.unitCanAttack(userUnit, aiUnit)) {
				return;
			}
			if (GameState.unitsAdjacent(userUnit, aiUnit)) {
				// user unit and ai unit are adjacent
				redrawGameTiles(out, gameState);
				performAttackAndCounterAttack(out, gameState, userUnit, aiUnit);
				gameState.clearActiveUnit();
				return;
			}
			// user unit and ai unit are NOT adjacent
			// try to find a tile that is both accessible to the user unit
			// and adjacent to the ai unit, so that the user unit can perform move + attack
			if (!gameState.unitCanMove(userUnit)) {
				return;
			}
			Tile targetTile = null;
			List<Tile> tilesAccessible = gameState.getTilesUnitCanMoveTo(userUnit);
			for (Tile tile : tilesAccessible) {
				if (GameState.tilesAdjacent(tile, tileClicked)) {
					targetTile = tile;
					break;
				}
			}
			if (targetTile == null) {
				return;
			}
			Action action = new Action() {
				@Override
				public void doAction(ActorRef out, GameState gameState) {
					performAttackAndCounterAttack(out, gameState, userUnit, aiUnit);
					gameState.clearActiveUnit();
				}
			};
			gameState.setPendingAction(action);
			// clear current tile effects
			redrawGameTiles(out, gameState);
			// tell unit to move
			userUnit.unitMove(out, gameState, targetTile);
		}
	}

	public void handleNoUnitOnTile(ActorRef out, GameState gameState, Tile tileClicked) {
		if (gameState.getActivateCard() != null) {
			trySummonUnitByCard(out, gameState, gameState.getActivateCard(), tileClicked);
			return;
		}
		if (gameState.getActiveUnit() != null) {
			Unit userUnit = gameState.getActiveUnit();
			if (!gameState.unitCanMove(userUnit)) {
				return;
			}
			List<Tile> accessibleTiles = gameState.getTilesUnitCanMoveTo(userUnit);
			for (Tile t : accessibleTiles) {
				if (t.equals(tileClicked)) {
					redrawGameTiles(out, gameState);
					userUnit.unitMove(out, gameState, tileClicked);
					return;
				}
			}
			BasicCommands.addPlayer1Notification(out, "Cannot move to tile: out of range", 3);
		}
	}

	private void trySummonUnitByCard(ActorRef out, GameState gameState, Card activeCard, Tile tile) {
		if (!activeCard.getIsCreature()) {
			BasicCommands.addPlayer1Notification(out, "This card cannot summon unit", 3);
			return;
		}
		redrawGameTiles(out, gameState);
		activeCard.summonUnit(out, gameState, tile);
	}

	private void tryApplyCardOnUnit(ActorRef out, GameState gameState, Card activeCard, Unit unit) {
		if (activeCard.getApplyOnUnitType() == Card.APPLY_NO_UNIT) {
			// cannot apply the current active card to a unit
			BasicCommands.addPlayer1Notification(out, "Cannot apply this card to a unit", 3);
			return;
		}
		if (activeCard.getApplyOnUnitType() == Card.APPLY_ENEMY_UNIT && gameState.isUserUnit(unit)) {
			// can only apply the current active card to an enemy unit
			BasicCommands.addPlayer1Notification(out, "Cannot apply this card to an ally unit", 3);
			return;
		}
		if (activeCard.getApplyOnUnitType() == Card.APPLY_ALLY_UNIT && gameState.isAiUnit(unit)) {
			// can only apply the current active card to an enemy unit
			BasicCommands.addPlayer1Notification(out, "Cannot apply this card to an enemy unit", 3);
			return;
		}
		Player user = gameState.getUserPlayer();
		if (user.getMana() < activeCard.getManacost()) {
			BasicCommands.addPlayer1Notification(out, "Not enough mana to play this card", 3);
			return;
		}
		// clear card related effect
		redrawGameTiles(out, gameState);
		int userMana = user.getMana();
		// cast spell on the unit
		activeCard.castSpell(out, gameState, unit);
		// update user's mana
		user.setMana(userMana - activeCard.getManacost());
		BasicCommands.setPlayer1Mana(out, user);
		// deactivate the used card
		gameState.clearActiveCard();
		// remove this card from user's hand card
		user.removeHandCardById(activeCard.getId());
		// delete this card from the UI
		int pos = user.getCardPosition(activeCard);
		BasicCommands.deleteCard(out, pos);
	}

	private void redrawGameTiles(ActorRef out, GameState gameState) {
		List<Tile> tiles = gameState.getGameTiles();
		for (Tile tile : tiles) {
			BasicCommands.drawTile(out, tile, Tile.TILE_NORMAL_MODE);
		}
	}

	private void performAttackAndCounterAttack(ActorRef out, GameState gameState, Unit u1, Unit u2) {
		if (!gameState.unitCanAttack(u1, u2)) {
			return;
		}
		u1.unitAttack(out, gameState, u2);
		if (u2.getHealth() > 0 && gameState.unitCanAttack(u2, u1)) {
			// perform counter attack
			u2.unitAttack(out, gameState, u1);
		}
	}
}


