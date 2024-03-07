package structures.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * A basic representation of of the Player. A player has health and mana.
 *
 * @author Dr. Richard McCreadie
 */
public class Player {
	public static final int MAX_HAND_CARD_NUM = 6;
	public static final int MAX_MANA = 9;

	protected int health;
	protected int mana;
	// Keep all units on the board that belong to this player
	// and the tiles occupied
	protected Map<Tile, Unit> tileAndUnits;
	protected List<Card> deckCards;
	protected Card[] handCards;

	public Player() {
		this.health = 20;
		this.mana = 0;
		this.tileAndUnits = new HashMap<>();
		this.deckCards = new ArrayList<>();
		this.handCards = new Card[MAX_HAND_CARD_NUM];
	}

	public Player(int health, int mana) {
		super();
		this.health = health;
		this.mana = mana;
		this.tileAndUnits = new HashMap<>();
		this.deckCards = new ArrayList<>();
		this.handCards = new Card[MAX_HAND_CARD_NUM];
	}

	public void playAI(ActorRef out, GameState gameState) {
		throw new UnsupportedOperationException("not implemented");
	}

	public void resumeAI(ActorRef out, GameState gameState) {
		throw new UnsupportedOperationException("not implemented");
	}

	public void resetStatus(ActorRef out, GameState gameState) {
		boolean isUserPlayer = this == gameState.getUserPlayer();
		setMana(0);
		if (isUserPlayer) {
			BasicCommands.setPlayer1Mana(out, this);
		} else {
			BasicCommands.setPlayer2Mana(out, this);
		}
		BasicCommands.sleep(50);

		drawOneNewCard();
		if (isUserPlayer) {
			for (int i = 0; i < Player.MAX_HAND_CARD_NUM; ++i) {
				BasicCommands.deleteCard(out, i + 1);
			}
			for (int i = 0; i < getHandCards().size(); i++) {
				Card card = getHandCardByPos(i);
				BasicCommands.drawCard(out, card, i + 1, Card.CARD_NORMAL_MODE);
			}
		}
		for (Unit u : getOwnUnits()) {
			u.setNewlySpawned(false);
			u.setStunned(false);
			u.setHasMoved(false);
			u.setHasAttacked(false);
		}
	}

	public void refreshManaByTurnNum(int turnNum) {
		if (mana == MAX_MANA) {
			return;
		}
		setMana(Math.min(MAX_MANA, turnNum + 1));
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public Map<Tile, Unit> getTileAndUnits() {
		return tileAndUnits;
	}

	public List<Card> getDeckCards() {
		return deckCards;
	}

	public void setDeckCards(List<Card> deckCards) {
		this.deckCards = deckCards;
	}

	public List<Unit> getOwnUnits() {
		return new ArrayList<>(tileAndUnits.values());
	}

	public Map<Tile, Unit> getAllUnitsAndTile() {
		return this.tileAndUnits;
	}

	public List<Tile> getAllTiles() {
		return new ArrayList<>(tileAndUnits.keySet());
	}

	public Unit getUnitByTile(Tile tile) {
		return tileAndUnits.get(tile);
	}

	public void putUnitOnTile(Tile tile, Unit unit) {
		if (tile.isOccupied()) {
			throw new IllegalStateException("put unit on occupied tile");
		}
		tile.setUnit(unit);
		unit.setPositionByTile(tile);
		tileAndUnits.put(tile, unit);
	}

	public void putCardAtPos(Card card, int pos) {
		if (pos < 0 || pos >= MAX_HAND_CARD_NUM) {
			throw new IllegalArgumentException("Invalid hand card pos: " + pos);
		}
		if (card == null) {
			throw new IllegalArgumentException("Invalid card: null");
		}
		handCards[pos] = card;
	}

	public Card drawOneNewCard() {
		if (deckCards.size() == 0) {
			return null;
		}
		if (getHandCards().size() == MAX_HAND_CARD_NUM) {
			// do not draw a new card when there are already 6 at hand
			return null;
		}
		int pos = GameState.nextRandInt(deckCards.size());
		Card newCard = deckCards.remove(pos);
		int p = 0;
		Card[] cardsNextTurn = new Card[MAX_HAND_CARD_NUM];
		for (int i = 0; i < MAX_HAND_CARD_NUM; i++) {
			if (handCards[i] != null) {
				cardsNextTurn[p++] = handCards[i];
			}
		}
		cardsNextTurn[p] = newCard;
		handCards = cardsNextTurn;
		return newCard;
	}

	public int removeHandCard(Card card) {
		for (int i = 0; i < MAX_HAND_CARD_NUM; i++) {
			if (handCards[i] == card) {
				handCards[i] = null;
				return i;
			}
		}
		throw new IllegalStateException("try remove non-existing card: " + card);
	}

	public Card getHandCardByPos(int pos) {
		if (pos < 0 || pos >= MAX_HAND_CARD_NUM) {
			throw new IllegalArgumentException("Invalid hand card position: " + pos);
		}
		if (handCards[pos] == null) {
			throw new IllegalStateException("No hand card at position: " + pos);
		}
		return handCards[pos];
	}

	public List<Card> getHandCards() {
		List<Card> results = new ArrayList<>();
		for (int i = 0; i < MAX_HAND_CARD_NUM; ++i) {
			Card card = handCards[i];
			if (card != null) {
				results.add(card);
			}
		}
		return results;
	}

	public int getHandCardPosition(Card card) {
		for (int i = 0; i < MAX_HAND_CARD_NUM; i++) {
			if (handCards[i] == card) {
				return i;
			}
		}
		return -1;
	}

	public void removeUnit(Unit targetUnit) {
		Iterator<Entry<Tile, Unit>> iterator = tileAndUnits.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Tile, Unit> entry = iterator.next();
			Tile tile = entry.getKey();
			Unit unit = entry.getValue();
			if (unit.equals(targetUnit)) {
				tile.clearUnit();
				iterator.remove();
				break;
			}
		}
	}

	public boolean hasUnit(Unit unit) {
		if (tileAndUnits == null || tileAndUnits.size() == 0) {
			return false;
		}
		for (Unit u : tileAndUnits.values()) {
			if (u.equals(unit)) {
				return true;
			}
		}
		return false;
	}

}
