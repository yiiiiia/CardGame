package events;

import java.util.HashSet;
import java.util.List;
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
		if (tileClicked.isOccupied() && gameState.isUserUnit(tileClicked.getUnit())) {
			Unit userUnit = tileClicked.getUnit();
			gameState.setActiveUnit(userUnit);
			if (gameState.unitCanMove(userUnit)) {
				hightlightTilesUnitCanMoveAndUnitsCanAttack(out, gameState, userUnit);
			}
			for (Tile tile : gameState.getAdjacentTiles(tileClicked)) {
				if (tile.isOccupied() && gameState.isAiUnit(tile.getUnit())) {
					Unit aiUnit = tile.getUnit();
					if (gameState.unitCanAttack(userUnit, aiUnit)) {
						BasicCommands.drawTile(out, tile, Tile.TILE_RED_MODE);
					}
				}
			}
		}
	}

	private void handleHasActiveUnit(ActorRef out, GameState gameState, Tile tileClicked) {
		Unit activeUnit = gameState.getActiveUnit();
		if (!tileClicked.isOccupied()) {
			gameState.redrawAllTiles(out);
			gameState.clearActiveUnit();
			if (!gameState.unitCanMove(activeUnit)) {
				BasicCommands.addPlayer1Notification(out, "Unit cannot move!", 5);
				return;
			}
			List<Tile> accessibleTiles = gameState.getTilesUnitCanMoveTo(activeUnit);
			if (accessibleTiles.contains(tileClicked)) {
				activeUnit.move(out, gameState, tileClicked);
			} else {
				BasicCommands.addPlayer1Notification(out, "Unit can not move: out of range!", 5);
			}
		} else {
			Unit unitOnTile = tileClicked.getUnit();
			if (unitOnTile == activeUnit) {
				return;
			}
			gameState.redrawAllTiles(out);
			gameState.clearActiveUnit();
			if (gameState.isUserUnit(unitOnTile)) {
				gameState.setActiveUnit(unitOnTile);
				if (gameState.unitCanMove(unitOnTile)) {
					hightlightTilesUnitCanMoveAndUnitsCanAttack(out, gameState, unitOnTile);
				}
				return;
			}
			// unitOnTile is ai unit
			if (GameState.unitsAdjacent(activeUnit, unitOnTile)) {
				if (!gameState.unitCanAttack(activeUnit, unitOnTile)) {
					String reason = gameState.whyUnitCannotAttack(activeUnit, unitOnTile);
					BasicCommands.addPlayer1Notification(out, "Cannot attack: " + reason, 5);
					return;
				}
				gameState.performAttackAndCounterAttack(out, activeUnit, unitOnTile);
				return;
			}
			if (!gameState.unitCanMove(activeUnit)) {
				BasicCommands.addPlayer1Notification(out, "Cannot attack: unit cannot move and target is not adjacnet!",
						5);
				return;
			}
			// user unit and ai unit are NOT adjacent
			// try to find a tile with the minimum distance that is both accessible to the
			// user unit and adjacent to the ai unit, so that the user unit can perform move
			// + attack
			Tile targetTile = gameState.findAttackPath(activeUnit, tileClicked, GameState.USER_MODE);
			if (targetTile == null) {
				BasicCommands.addPlayer1Notification(out, "Cannot attack: target is out of range!", 5);
				return;
			}
			Action action = new Action() {
				@Override
				public void doAction(ActorRef out, GameState gameState) {
					gameState.performAttackAndCounterAttack(out, activeUnit, unitOnTile);
				}
			};
			gameState.setPendingAction(action);
			activeUnit.move(out, gameState, targetTile);
		}
	}

	private void handleHasActiveCard(ActorRef out, GameState gameState, Tile tileClicked) {
		Card activeCard = gameState.getActiveCard();
		gameState.redrawAllTiles(out);
		gameState.clearActiveCard(out);
		if (activeCard.getCardname().equals(WraithlingSwarmCard.CARD_NAME)) {
			// Wraithling Swarm is special in that it is a spell card, but summons creatures
			// on any vacant tile
			if (tileClicked.isOccupied()) {
				BasicCommands.addPlayer1Notification(out, "Cannot summon creature: occupied tile!", 5);
				return;
			}
			if (activeCard.getManacost() > gameState.getUserPlayer().getMana()) {
				BasicCommands.addPlayer1Notification(out, "Cannot summon creature: not enough mana!", 5);
				return;
			}
			activeCard.castSpell(out, gameState, tileClicked);
		} else if (activeCard.getIsCreature()) {
			if (tileClicked.isOccupied()) {
				BasicCommands.addPlayer1Notification(out, "Cannot summon creature: occupied tile!", 5);
				return;
			}
			List<Tile> tilesForSummon = gameState.getTilesForSummon(GameState.USER_MODE);
			if (!tilesForSummon.contains(tileClicked)) {
				BasicCommands.addPlayer1Notification(out, "Cannot summon creature: tile out of range!", 5);
				return;
			}
			if (activeCard.getManacost() > gameState.getUserPlayer().getMana()) {
				BasicCommands.addPlayer1Notification(out, "Cannot summon creature: not enough mana!", 5);
				return;
			}
			activeCard.summonUnitOnTile(out, gameState, tileClicked, GameState.USER_MODE);
			if (activeCard.isUsed()) {
				gameState.deleteUserCard(out, activeCard);
			}
		} else {
			if (!tileClicked.isOccupied()) {
				BasicCommands.addPlayer1Notification(out, "Cannot cast spell: no unit on tile!", 5);
				return;
			}
			if (activeCard.getManacost() > gameState.getUserPlayer().getMana()) {
				BasicCommands.addPlayer1Notification(out, "Cannot cast spell: not enough mana!", 5);
				return;
			}
			activeCard.castSpell(out, gameState, tileClicked);
			if (activeCard.isUsed()) {
				gameState.deleteUserCard(out, activeCard);
			}
		}
	}

	private void hightlightTilesUnitCanMoveAndUnitsCanAttack(ActorRef out, GameState gameState, Unit unit) {
		List<Tile> tilesCanMove = gameState.getTilesUnitCanMoveTo(unit);
		Set<Tile> tilesCanAttack = new HashSet<>();
		List<Tile> aiUnitTiles = gameState.getAllAITiles();
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
		for (Tile t : tilesCanMove) {
			BasicCommands.drawTile(out, t, Tile.TILE_WHITE_MODE);
		}
		for (Tile t : tilesCanAttack) {
			BasicCommands.drawTile(out, t, Tile.TILE_RED_MODE);
		}
	}
}
