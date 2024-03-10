package events;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Action;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.card.WraithlingSwarmCard;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case a tile. The event returns the x (horizontal) and y (vertical) indices of
 * the tile that was clicked. Tile indices start at 1.
 * 
 * { messageType = “tileClicked” tilex = <x index of the tile> tiley = <y index
 * of the tile> }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor {
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		Tile tileClicked = gameState.getTileByPos(tilex, tiley);
		if (tileClicked == null) {
			throw new IllegalStateException(String.format("tile not exists, tileX: %d, tileY: %d", tilex, tiley));
		}
		if (gameState.getActiveUnit() != null) {
			handleHasActiveUnit(out, gameState, tileClicked);
			return;
		}
		if (gameState.getActiveCard() != null) {
			handleHasActiveCard(out, gameState, tileClicked);
			return;
		}
		handleNoActiveItem(out, gameState, tileClicked);
	}

	private void handleNoActiveItem(ActorRef out, GameState gameState, Tile tileClicked) {
		if (tileClicked.isOccupied() && gameState.isUserUnit(tileClicked.getUnit())) {
			boolean hasEffect = false;
			Unit userUnit = tileClicked.getUnit();
			gameState.setActiveUnit(userUnit);
			if (gameState.unitCanMove(userUnit)) {
				hightlightTilesUnitCanMoveAndUnitsCanAttack(out, gameState, userUnit);
				hasEffect = true;
			}
			for (Tile tile : gameState.getAdjacentTiles(tileClicked)) {
				if (tile.isOccupied() && gameState.isAiUnit(tile.getUnit())) {
					Unit aiUnit = tile.getUnit();
					if (gameState.unitCanAttack(userUnit, aiUnit)) {
						hasEffect = true;
						gameState.drawAndRecordHighlightedTile(out, tile, Tile.TILE_RED_MODE);
					}
				}
			}
			if (!hasEffect) {
				String reason = gameState.whyUnitCannotMove(userUnit);
				BasicCommands.addPlayer1Notification(out, "Unit cannot move and there's no target to attack:\n" + reason,
						3);
			}
		}
	}

	private void handleHasActiveUnit(ActorRef out, GameState gameState, Tile tileClicked) {
		Unit activeUnit = gameState.getActiveUnit();
		Unit unitOnTile = tileClicked.getUnit();
		if (unitOnTile != null) {
			if (unitOnTile == activeUnit) {
				return;
			}
			if (gameState.isUserUnit(unitOnTile)) {
				gameState.clearActiveUnit();
				gameState.clearHighlightedTiles(out);
				handleNoActiveItem(out, gameState, tileClicked);
			} else {
				tryAttackAIUnit(out, gameState, activeUnit, unitOnTile);
			}
		} else {
			if (!gameState.unitCanMove(activeUnit)) {
				String reason = gameState.whyUnitCannotMove(activeUnit);
				BasicCommands.addPlayer1Notification(out, "Unit cannot move: " + reason, 3);
				return;
			}
			Set<Tile> accessibleTiles = gameState.getTilesUnitCanMoveTo(activeUnit);
			if (accessibleTiles.contains(tileClicked)) {
				gameState.clearActiveUnit();
				gameState.clearHighlightedTiles(out);
				activeUnit.move(out, gameState, tileClicked);
			} else {
				BasicCommands.addPlayer1Notification(out, "Unit can not move: out of range!", 3);
			}
		}
	}

	private void handleHasActiveCard(ActorRef out, GameState gameState, Tile tileClicked) {
		Card activeCard = gameState.getActiveCard();
		if (activeCard.getManacost() > gameState.getUserPlayer().getMana()) {
			BasicCommands.addPlayer1Notification(out, "Cannot use this card: not enough mana!", 3);
			gameState.clearActiveCard(out);
			gameState.clearHighlightedTiles(out);
			return;
		}
		if (activeCard.getClass() == WraithlingSwarmCard.class) {
			// Wraithling Swarm is special in that it is a spell card, but summons creatures
			// on any vacant tile
			if (tileClicked.isOccupied()) {
				BasicCommands.addPlayer1Notification(out, "Cannot summon creature on occupied tile!", 3);
			} else {
				activeCard.castSpell(out, gameState, tileClicked);
			}
			return;
		}
		if (activeCard.getIsCreature()) {
			if (tileClicked.isOccupied()) {
				Unit unitOnTile = tileClicked.getUnit();
				if (gameState.isUserUnit(unitOnTile)) {
					gameState.clearActiveCard(out);
					gameState.clearHighlightedTiles(out);
					handleNoActiveItem(out, gameState, tileClicked);
				} else {
					BasicCommands.addPlayer1Notification(out, "Cannot summon creature on occupied tile!", 3);
				}
			} else {
				Set<Tile> tilesForSummon = gameState.getTilesForSummon(GameState.USER_MODE);
				if (!tilesForSummon.contains(tileClicked)) {
					BasicCommands.addPlayer1Notification(out, "Cannot summon creature: tile out of range!", 3);
					return;
				}
				gameState.clearActiveCard(out);
				gameState.clearHighlightedTiles(out);
				activeCard.summonUnitOnTile(out, gameState, tileClicked, GameState.USER_MODE);
				gameState.deleteUserCard(out, activeCard);
			}
		} else {
			if (!tileClicked.isOccupied()) {
				BasicCommands.addPlayer1Notification(out, "Cannot cast spell on empty tile!", 3);
				return;
			}
			String reason = activeCard.canCastSpellOnUnit(gameState, tileClicked.getUnit());
			if (reason == null || reason.isEmpty()) {
				gameState.clearActiveCard(out);
				gameState.clearHighlightedTiles(out);
				activeCard.castSpell(out, gameState, tileClicked);
				gameState.deleteUserCard(out, activeCard);
			} else {
				BasicCommands.addPlayer1Notification(out, "Cannot use this card: " + reason, 3);
			}
		}
	}

	private void hightlightTilesUnitCanMoveAndUnitsCanAttack(ActorRef out, GameState gameState, Unit unit) {
		Set<Tile> tilesCanMove = gameState.getTilesUnitCanMoveTo(unit);
		Set<Tile> tilesCanAttack = new HashSet<>();
		if (unit.getAttack() > 0) {
			Set<Tile> aiUnitTiles = gameState.getAllAITiles();
			for (Tile aiTile : aiUnitTiles) {
				for (Tile tileAccessible : tilesCanMove) {
					if (GameState.tilesAdjacent(aiTile, tileAccessible)) {
						if (gameState.getAiProvokeAreas().contains(tileAccessible)
								&& !aiTile.getUnit().hasProvokeAbility()) {
							continue;
						}
						tilesCanAttack.add(aiTile);
					}
				}
			}
		}
		for (Tile t : tilesCanMove) {
			gameState.drawAndRecordHighlightedTile(out, t, Tile.TILE_WHITE_MODE);
		}
		for (Tile t : tilesCanAttack) {
			gameState.drawAndRecordHighlightedTile(out, t, Tile.TILE_RED_MODE);
		}
	}

	private void tryAttackAIUnit(ActorRef out, GameState gameState, Unit userUnit, Unit aiUnit) {
		if (GameState.unitsAdjacent(userUnit, aiUnit)) {
			if (!gameState.unitCanAttack(userUnit, aiUnit)) {
				String reason = gameState.whyUnitCannotAttack(userUnit, aiUnit);
				BasicCommands.addPlayer1Notification(out, "Cannot attack unit clicked: " + reason, 3);
			} else {
				gameState.clearActiveUnit();
				gameState.clearHighlightedTiles(out);
				gameState.performAttackAndCounterAttack(out, userUnit, aiUnit);
			}
		} else {
			if (!gameState.unitCanMove(userUnit)) {
				BasicCommands.addPlayer1Notification(out, "Cannot attack: unit cannot move and target is not adjacnet!",
						3);
				return;
			}
			// user unit and ai unit are NOT adjacent
			// try to find a tile with the minimum distance that is both accessible to the
			// user unit and adjacent to the ai unit, so that the user unit can move then
			// attack
			Tile aiUnitTile = gameState.getUnitTile(aiUnit);
			Tile targetTile = gameState.findAttackPath(userUnit, aiUnitTile, GameState.USER_MODE);
			if (targetTile == null) {
				BasicCommands.addPlayer1Notification(out, "Cannot attack: target is out of range!", 3);
				return;
			}
			Action action = new Action() {
				@Override
				public void doAction(ActorRef out, GameState gameState) {
					gameState.performAttackAndCounterAttack(out, userUnit, aiUnit);
				}
			};
			gameState.clearActiveUnit();
			gameState.clearHighlightedTiles(out);
			gameState.setPendingAction(action);
			userUnit.move(out, gameState, targetTile);
		}
	}
}
