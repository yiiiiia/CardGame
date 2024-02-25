package structures.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A basic representation of of the Player. A player
 * has health and mana.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Player {
	public static final int MAX_HAND_CARD_NUM = 6;
	private int health;
	private int mana;
	// All units on the board that belong to this player
	private Map<Tile, Unit> allUnits;
	// private ArrayList<Unit> m_allUnit;//可召唤的所有生物
	// private  Map<Tile,Unit> m_unit;//现在场上的单位
	private List<Card> handCard;//现在的手牌
	private List<Card> cardsRemain;//牌库剩余的牌

	public Player() {
		super();
		this.health = 20;
		this.mana = 0;
		this.allUnits = new HashMap<>();
	}

	public Player(int health, int mana) {
		super();
		this.health = health;
		this.mana = mana;
		this.allUnits = new HashMap<>();
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

	public Unit getUnitByTile(Tile tile) {
		return allUnits.get(tile);
	}

	public void addUnitOnTile(Tile tile, Unit unit) {
		tile.setUnit(unit);
		allUnits.put(tile, unit);
	}

	public List<Unit> getAllUnits() {
		List<Unit> units = new ArrayList<>();
		units.addAll(allUnits.values());
		return units;	
	}

	public void removeHandCardById(int id) {
		// TODO implementation
	}

	public List<Card> getHandCards() {
		// TODO implementation
		return null;
	}

	public void removeUnit(Unit unit) {
		Iterator<Entry<Tile, Unit>> iterator = allUnits.entrySet().iterator();
		while (iterator.hasNext()) {
			Tile tile = iterator.next().getKey();
			Unit u = iterator.next().getValue();
			if (u.getId() == unit.getId()) {
				tile.clearUnit();
				iterator.remove();
			}
		}
	}

	public boolean hasUnit(Unit unit) {
		if (allUnits == null || allUnits.size() == 0) {
			return false;
		}
		for (Unit u : allUnits.values()) {
			if (u.getId() == unit.getId()) {
				return true;
			}
		}
		return false;
	}

	public int getCardPosition(Card card) {
		// TODO: implementation
		return 0;
	}
}
