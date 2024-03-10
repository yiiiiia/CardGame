package structures.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.card.BeamShockCard;
import structures.basic.card.SundropElixirCard;
import structures.basic.card.TrueStrikeCard;
import structures.basic.unit.YoungFlamewing;

public class AiPlayer extends Player {

	public static final Logger logger = LoggerFactory.getLogger(AiPlayer.class);

	private boolean suspension;

	public AiPlayer(int health, int mana) {
		super(health, mana);
	}

	@Override
	// access to AI logic from EndTurnClocked event
	public void playAI(ActorRef out, GameState gameState) {
		BasicCommands.setPlayer2Mana(out, this);
		BasicCommands.setPlayer2Health(out, this);
		// execute ai game logic
		playGameLogic(out, gameState);
		if (!suspension) {
			resetAndReturnControlToUser(out, gameState);
		}
	}

	@Override
	public void resumeAI(ActorRef out, GameState gameState) {
		clearSuspension();
		playGameLogic(out, gameState);
		if (!suspension) {
			resetAndReturnControlToUser(out, gameState);
		}
	}

	private void resetAndReturnControlToUser(ActorRef out, GameState gameState) {
		resetStatus(out, gameState);
		gameState.addTurn();
		gameState.getUserPlayer().refreshManaByTurnNum(gameState.getTurn());
		BasicCommands.setPlayer1Mana(out, gameState.getUserPlayer());
		gameState.setGameMode(GameState.USER_MODE);
		BasicCommands.addPlayer1Notification(out, "Back to user mode", 3);
	}

	private void playGameLogic(ActorRef out, GameState gameState) {
		tryMoveAvatar(out, gameState);
		tryUseCards(out, gameState);
		tryMoveCreatures(out, gameState);
		tryAttack(out, gameState);
	}

	private void tryMoveAvatar(ActorRef out, GameState gameState) {
		if (suspension) {
			return;
		}
		if (!gameState.unitCanMove(gameState.getAiAvatar())) {
			return;
		}
		Unit aiAvatar = gameState.getAiAvatar();
		Unit userAvatar = gameState.getUserAvatar();
		Set<Tile> tiles = gameState.getTilesUnitCanMoveTo(aiAvatar);
		if (tiles.isEmpty()) {
			return;
		}
		if (GameState.unitsAdjacent(aiAvatar, userAvatar)) {
			Tile adjustTo = gameState.needAdjustPosition(gameState.getUnitTile(userAvatar),
					gameState.getUnitTile(aiAvatar));
			if (adjustTo != null) {
				BasicCommands.addPlayer1Notification(out, "Ai avatar move", 3);
				aiAvatar.move(out, gameState, adjustTo);
				setSuspension();
			}
			return;
		}
		Tile targetTile = gameState.findTileClosestToUnit(userAvatar, tiles);
		Tile userAvatarTile = gameState.getUnitTile(userAvatar);
		Tile aiAvatarTile = gameState.getUnitTile(aiAvatar);
		int d1 = GameState.distanceBetweenTiles(userAvatarTile, targetTile);
		int d2 = GameState.distanceBetweenTiles(userAvatarTile, aiAvatarTile);
		if (d1 < d2) {
			BasicCommands.addPlayer1Notification(out, "Ai avatar move", 3);
			aiAvatar.move(out, gameState, targetTile);
			setSuspension();
		}
	}

	private void tryUseCards(ActorRef out, GameState gameState) {
		if (suspension) {
			return;
		}
		List<Card> handCards = getHandCards();
		for (Card card : handCards) {
			if (card.getManacost() > this.mana) {
				continue;
			}
			// card that summon creatures
			if (card.getIsCreature()) {
				// 1. get all tiles that a creature can stand on
				Set<Tile> tiles = gameState.getTilesForSummon(GameState.AI_MODE);
				Tile target = gameState.findTileClosestToUnit(gameState.getUserAvatar(), tiles);
				BasicCommands.addPlayer1Notification(out, "Ai use card: " + card.getCardname(), 3);
				card.summonUnitOnTile(out, gameState, target, GameState.AI_MODE);
				removeHandCard(card);
			} else {
				tryUseSpellCards(out, gameState, card);
			}
		}
	}

	private void tryUseSpellCards(ActorRef out, GameState gameState, Card card) {
		switch (card.getCardname()) {
		case SundropElixirCard.CARD_NAME:
			tryUseSundrop(out, gameState, card);
			break;
		case TrueStrikeCard.CARD_NAME:
			tryUseTrueStrike(out, gameState, card);
			break;
		case BeamShockCard.CARD_NAME:
			tryUseBeamShock(out, gameState, card);
			break;
		default:
			throw new IllegalStateException("AI player cannot use card: " + card.getCardname());
		}
	}

	private void tryUseSundrop(ActorRef out, GameState gameState, Card card) {
		List<Unit> allMyUnits = getOwnUnits();
		for (Unit unit : allMyUnits) {
			if (unit.getHealth() <= unit.getMaxHealth()) {
				if (unit.getMaxHealth() - unit.getHealth() >= 4) {
					Tile tile = gameState.getUnitTile(unit);
					BasicCommands.addPlayer1Notification(out, "Ai use card: " + card.getCardname(), 3);
					card.castSpell(out, gameState, tile);
					logger.info("card to be removeed: " + card);
					removeHandCard(card);
				}
			} else {
				String errmsg = String.format("Unit health is larger than its max health: %s", unit.toString());
				throw new IllegalStateException(errmsg);
			}
		}
	}

	private void tryUseTrueStrike(ActorRef out, GameState gameState, Card card) {
		Unit userAvatar = gameState.getUserAvatar();
		Tile tile = gameState.getUnitTile(userAvatar);
		BasicCommands.addPlayer1Notification(out, "Ai use card: " + card.getCardname(), 3);
		card.castSpell(out, gameState, tile);
		removeHandCard(card);
	}

	private void tryUseBeamShock(ActorRef out, GameState gameState, Card card) {
		Set<Tile> tiles = gameState.getAllUserTiles();
		Iterator<Tile> iter = tiles.iterator();
		while (iter.hasNext()) {
			// exclude user avatar
			Tile t = iter.next();
			if (t.getUnit() == gameState.getUserAvatar()) {
				iter.remove();
				break;
			}
		}
		while (!tiles.isEmpty()) {
			Tile target = gameState.findTileClosestToUnit(gameState.getAiAvatar(), tiles);
			if (target.getUnit().isStunned()) {
				tiles.remove(target);
				continue;
			}
			BasicCommands.addPlayer1Notification(out, "AI use card: " + card.getCardname(), 3);
			card.castSpell(out, gameState, target);
			removeHandCard(card);
			break;
		}
	}

	private void tryMoveCreatures(ActorRef out, GameState gameState) {
		if (suspension) {
			return;
		}
		for (Unit aiUnit : this.getOwnUnits()) {
			if (gameState.unitCanMove(aiUnit)) {
				if (aiUnit.getClass() == YoungFlamewing.class) {
					// YoungFlamewing is special that it can mvoe any unoccupied space on the board
					YoungFlamewing yf = (YoungFlamewing) aiUnit;
					yf.performAbility(AbilityType.FLYING, out, gameState);
					if (aiUnit.getHasMoved()) {
						setSuspension();
						return;
					}
					continue;
				}
				Set<Tile> tilesCurUnitCanMove = gameState.getTilesUnitCanMoveTo(aiUnit);
				if (tilesCurUnitCanMove.isEmpty()) {
					continue;
				}
				Set<Tile> userTiles = gameState.getAllUserTiles();
				Tile aiUnitTile = gameState.getUnitTile(aiUnit);
				List<Tile> nearbyUserUnitTiles = new ArrayList<>();
				for (Tile userTile : userTiles) {
					if (GameState.tilesAdjacent(aiUnitTile, userTile)) {
						nearbyUserUnitTiles.add(userTile);
					}
				}
				if (!nearbyUserUnitTiles.isEmpty()) {
					// already has user units nearby
					for (Tile nearbyUserTile : nearbyUserUnitTiles) {
						Tile adjstToTile = gameState.needAdjustPosition(nearbyUserTile, aiUnitTile);
						if (adjstToTile != null && tilesCurUnitCanMove.contains(adjstToTile)) {
							BasicCommands.addPlayer1Notification(out, "Move AI creatures", 3);
							aiUnit.move(out, gameState, adjstToTile);
							setSuspension();
							return;
						}
					}
					continue;
				}
				Tile userUnitTile = gameState.findTileClosestToUnit(aiUnit, userTiles);
				Tile destination = gameState.findTileClosestToTile(userUnitTile, tilesCurUnitCanMove);
				int d1 = GameState.distanceBetweenTiles(userUnitTile, destination);
				int d2 = GameState.distanceBetweenTiles(userUnitTile, aiUnitTile);
				if (d1 < d2 || GameState.tilesAdjacent(userUnitTile, destination)) {
					BasicCommands.addPlayer1Notification(out, "Move AI creatures", 3);
					aiUnit.move(out, gameState, destination);
					setSuspension();
					return;
				}
			}
		}
	}

	private void tryAttack(ActorRef out, GameState gameState) {
		if (suspension) {
			return;
		}
		for (Unit aiUnit : this.getOwnUnits()) {
			if (!gameState.unitCanAttack(aiUnit, null)) {
				continue;
			}
			Tile aiUnitTile = gameState.getUnitTile(aiUnit);
			List<Unit> adjacentUserUnits = findAdjacentUserUnits(gameState, aiUnitTile);
			sortUserUnits(adjacentUserUnits, gameState);
			for (Unit userUnit : adjacentUserUnits) {
				if (gameState.unitCanAttack(aiUnit, userUnit)) {
					gameState.performAttackAndCounterAttack(out, aiUnit, userUnit);
					if (gameState.isGameOver()) {
						return;
					}
				}
			}
		}
	}

	private void sortUserUnits(List<Unit> units, GameState gameState) {
		Comparator<Unit> byHealthAndAttack = new Comparator<Unit>() {
			@Override
			public int compare(Unit u1, Unit u2) {
				// always put user avater first
				if (u1 == gameState.getUserAvatar()) {
					return -1;
				}
				if (u2 == gameState.getUserAvatar()) {
					return 1;
				}
				int comp = u1.getHealth() - u2.getHealth();
				if (comp != 0) {
					return comp;
				}
				return u2.getAttack() - u1.getAttack();
			}
		};
		Collections.sort(units, byHealthAndAttack);
	}

	private List<Unit> findAdjacentUserUnits(GameState gameState, Tile aiUnitTile) {
		List<Unit> userUnits = new ArrayList<>();
		for (Tile tile : gameState.getAdjacentTiles(aiUnitTile)) {
			if (tile.isOccupied() && gameState.isUserUnit(tile.getUnit())) {
				userUnits.add(tile.getUnit());
			}
		}
		return userUnits;
	}

	private void setSuspension() {
		suspension = true;
	}

	private void clearSuspension() {
		suspension = false;
	}

}