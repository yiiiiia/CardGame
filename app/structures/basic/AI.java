package draft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Action;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;

public class AI extends Player {

	public AI(int health, int mana) {
		super(health, mana);
	}

	public void aiAction(GameState gameState, ActorRef out, JsonNode message) {
		this.useSpellCard(gameState, out, message);
		this.useCreatureCard(gameState, out, message);
		this.moveOrAttack(gameState, out, message);
	}

	/*
	 * The AI strategy uses effect cards first and directly reduces the number of
	 * players first. The stun card acts on the unit with the highest attack power.
	 * Healing cards are used for the unit that has lost the most health, or the
	 * unit with the highest attack power, or the unit that is most vulnerable to
	 * attack. Attack cards are used when the current health value is less than
	 */
//Equal to 2, if not, attack with the highest attack power.
//The mana cost of summoning cards increases from small to large. 
//The summoning location is prioritized near Avater. Rush cards should be as close to local units as possible.
//Attack and movement: attack the nearest first */
	public List<Card> haveSpell() {
		List<Card> spell = new ArrayList<Card>();
		for (Card cur : this.getHandCards()) {
			if (!cur.isCreature()) {
				spell.add(cur);
			}
		}
		return spell;
	}

	public List<Card> haveCreature()
	{
		List<Card> creature = new ArrayList<Card>();
		for (Card cur : this.getHandCards()) {
			if (cur.isCreature()) {
				creature.add(cur);
			}
		}
		return creature;
	}
//use spell card
	public void useSpellCard(GameState gameState, ActorRef out, JsonNode message) {
		List<Card> spellCard = this.haveSpell();
		if (spellCard == null) {
			return;
		} else {
			for (Card cur : spellCard) {
				String curname = cur.getCardname();
				if (this.getMana() >= cur.getManacost()) {
					switch (curname) {
					case "SundropElixir":
						useSundrop(cur, gameState, out, message);// Optimize the timing of use later
						break;
					case "TrueStrike":
						useTrueStrike(cur, gameState, out, message);
						break;
					case "BeamShockCard":
						Tile tile = this.havaAdUnit(gameState);
						if (tile != null) {
							useStun(cur, tile, gameState, out, message);
						} // Later upgrade to use bean shock after attacking once
						break;
					}
				}
			}
		}

	}

//use Beam Shock card
	public void useStun(Card card, Tile tile, GameState gameState, ActorRef out, JsonNode message) {

		// The current card is not locked

		if (!gameState.getUserPlayer().getAllUnitsAndTile().get(tile).getStunned()) {
			Card c1 = new BeamShockCard();
			if (c1.performSpell(out, gameState, gameState.getUserPlayer().getAllUnits().get(cur))) {
				c1.highlightTiles(out, gameState);

			}
			this.removeHandCard(card);
		}

	}

//use Sundrop card
	public void useSundrop(Card card, GameState gameState, ActorRef out, JsonNode message) {
		Tile attackMax = null;
		int maxLostHp = 0;
		for (Tile cur : gameState.getUserPlayer().getAllUnitsAndTile().keySet()) {
			int nowLostHp = gameState.getUserPlayer().getAllUnitsAndTile().get(cur).getMaxHealth()
					- gameState.getUserPlayer().getAllUnits().get(cur).getCurHealth();
			if (maxLostHp < nowLostHp) {
				attackMax = cur;
				maxLostHp = nowLostHp;
			}
		}
		
		//There are currently no injured units
		if(maxLostHp==0)
		{
			return;
		}

		Card c2 = new SundropElixirCard();
		if (c2.performSpell(out, gameState, gameState.getUserPlayer().getAllUnitsAndTile().get(attackMax))) {
			c2.highlightTiles(out, gameState);

		}
		this.removeHandCardById(card.getId());
		this.setMana(this.getMana() - 1);

	}

//use TrueStrike card
	public void useTrueStrike(Card card, GameState gameState, ActorRef out, JsonNode message) {
		Tile healthMin = null;
		for (Tile cur : gameState.getUserPlayer().getAllUnitsAndTile().keySet()) {
			if (gameState.getUserPlayer().getAllUnitsAndTile().get(cur).getCurHealth() < gameState.getUserPlayer()
					.getAllUnitsAndTile().get(healthMin).getCurHealth()) {
				healthMin = cur;
			}
		}

		Card c3 = new TrueStrikeCard();
		if (c3.performSpell(out, gameState, gameState.getUserPlayer().getAllUnits().get(cur))) {
			c3.highlightTiles(out, gameState);

		}
		this.removeHandCardById(card.getId());
		this.setMana(this.getMana() - 1);

	}

//Arrange cards from least mana cost to most mana cost,After reaching a certain number of rounds, 
//start to arrange from the largest mana consumption to the smallest mana consumption.
	class PersonComparator implements Comparator<Card> {
		@Override
		public int compare(Card c1, Card c2) {
			return Integer.compare(c1.getManacost(), c2.getManacost());
		}
	}
	
	class PersonComparator1 implements Comparator<Card> {
		@Override
		public int compare(Card c1, Card c2) {
			return Integer.compare(c2.getManacost(), c1.getManacost());
		}
	}

//use creature card;
	public void useCreatureCard(GameState gameState, ActorRef out, JsonNode message) {
		List<Card> creatureCard=this.haveCreature();
		if (this.getMana() == 0||creatureCard==null) {
			return;
		} else {
			if(gameState.getTurn()<=7)
			{Collections.sort(creatureCard, new PersonComparator());}
			else
			{Collections.sort(creatureCard, new PersonComparator1());}
			for (Card cur : creatureCard) {
				if (this.getMana() < cur.getManacost()) {
					continue;
				}
				Tile tile = placeableArea(gameState);
				if (tile != null) {
					cur.summonUnit(out, gameState, tile.getTilex(), tile.getTiley());
				}

			}
		}

	}

//Determine whether it is a placeable area
	public boolean isPlaceBle(int x, int y, GameState gameState) {
		if (x < 0 || x > 8 || y < 0 || y > 4
				|| gameState.getUserPlayer().getAllUnitsAndTile().containsKey(gameState.getTileByPos(x, y)))// 这里得再加AI类的unit判断
		{
			return false;
		}
		return true;
	}

//Returns the Tile that can summon creatures
	public Tile placeableArea(GameState gameState)// Later, can add some strategies on where to put it.
	{
		for (Tile cur : this.getAllUnitsAndTile().keySet()) {
			int x = cur.getTilex() - 1;
			int y = cur.getTiley() - 1;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (isPlaceBle(x + i, y + j, gameState)) {
						return gameState.getTileByPos(x + i, y + j);
					}

				}
			}

		}
		return null;
	}

//Attack or move. If there is no target to attack, move towards the nearest target and optimize later.
	public void moveOrAttack(GameState gameState, ActorRef out, JsonNode message) {
		for (Tile cur : this.getAllUnitsAndTile().keySet()) {
			Unit aiUnit = gameState.getAiPlayer().getUnitByTile(cur);
			Unit userUnit = gameState.getUserPlayer().getAllUnitsAndTile().get(getClosestTile(cur, gameState));
			List<Tile> tilesAccessible = gameState.getTilesUnitCanMoveTo(aiUnit);

			// The first branch, adjacent units can attack
			if (gameState.unitsAdjacent(aiUnit, userUnit)) {
				// user unit and ai unit are adjacent
				aiUnit.unitAttack(out, gameState, userUnit);
				if (userUnit.getHealth() > 0) {
					// perform counter attack
					userUnit.unitAttack(out, gameState, aiUnit);
				}
				gameState.clearActiveUnit();
				break;
			}
			// Adjacent units have no attacking units
			else {
				Tile targetTile = null;
				for (Tile tile : tilesAccessible) {
					if (gameState.tilesAdjacent(tile, getClosestTile(cur, gameState))) {
						targetTile = tile;
						break;
					}
				}
				// After moving, there is no attacking unit, and the super nearest target moves
				if (targetTile == null) {
					// cannot perform move + attack
					int distancex = getClosestTile(cur, gameState).getTilex() - cur.getTilex();
					int distancey = getClosestTile(cur, gameState).getTiley() - cur.getTiley();
					if (Math.abs(distancex) > Math.abs(distancey)) {
						aiUnit.unitMove(out, gameState,
								gameState.getTileByPos(cur.getTilex() + Integer.signum(distancex) * 2, cur.getTiley()));
					} else if (Math.abs(distancex) == Math.abs(distancey)) {
						aiUnit.unitMove(out, gameState,
								gameState.getTileByPos(cur.getTilex() + Integer.signum(distancex),
										cur.getTiley() + Integer.signum(distancey)));
					} else {
						aiUnit.unitMove(out, gameState,
								gameState.getTileByPos(cur.getTilex(), cur.getTiley() + Integer.signum(distancey) * 2));
					}
					break;
				}
				// Attack after moving
				else {
					// Determine whether it can be moved
					boolean reason = gameState.unitCanMove(aiUnit);
					if (!reason) {

						break;
					}
					Action action = new Action() {
						@Override
						public void doAction(ActorRef out, GameState gameState) {
							performAttackAndCounterAttack(out, gameState, aiUnit, userUnit);
							gameState.clearActiveUnit();

						}
					};

					gameState.setPendingAction(action);
					// clear current tile effects
					// tell unit to move
					aiUnit.unitMove(out, gameState, targetTile);
				}
			}
		}
	}

//Determine whether there is an attack target. This will be changed later. It is not used at the moment.；
	public boolean haveAttack(Tile tile, GameState gameState, ActorRef out, JsonNode message) {
		return true;
	}

//Find the nearest attacking unit,Spread outward with the current position as the center
	public Tile getClosestTile(Tile aitile, GameState gameState)// Ensure that the first round has been completed for a
																// week to determine whether there is a provoke card.
	{
		int rows = 9;// Rows
		int cols = 5;// columns
		boolean[][] visited = new boolean[rows][cols];
		int k = 0;// Counter, used to determine whether there is a provoke card in the first week
		Queue<int[]> queue = new LinkedList<>();
		queue.offer(new int[] { aitile.getTilex(), aitile.getTiley() });
		visited[aitile.getTilex()][aitile.getTiley()] = true;
		Tile targetTile = null;
		int[][] directions = { { -1, 0 }, { -1, -1 }, { 1, 0 }, { 1, 1 }, { 0, -1 }, { -1, -1 }, { 0, 1 }, { 1, 1 } };

		while (!queue.isEmpty()) {
			int[] currentCell = queue.poll();
			int currentRow = currentCell[0];
			int currentCol = currentCell[1];

			if (k > 8 && gameState.getUserPlayer().getAllUnitsAndTile()
					.containsKey(gameState.getTileByPos(currentRow, currentCol))) {
				return gameState.getTileByPos(currentRow, currentCol); // Find the nearest tile
			} else {
				if (k == 8 && targetTile != null) {
					return targetTile;
				}
				// Assume that the ID of the provoke card is 1
				if (gameState.getUserPlayer().getAllUnitsAndTile()
						.containsKey(gameState.getTileByPos(currentRow, currentCol))
						&& gameState.getUserPlayer().getAllUnitsAndTile()
								.get(gameState.getTileByPos(currentRow, currentCol)).getId() == 1) {
					return gameState.getTileByPos(currentRow, currentCol);
				}
				if (gameState.getUserPlayer().getAllUnitsAndTile()
						.containsKey(gameState.getTileByPos(currentRow, currentCol))) {
					targetTile = gameState.getTileByPos(currentRow, currentCol);
				}
			}
			k++;

			for (int[] direction : directions) {
				int newRow = currentRow + direction[0];
				int newCol = currentCol + direction[1];

				if (isValid(newRow, newCol, rows, cols) && !visited[newRow][newCol]) {
					queue.offer(new int[] { newRow, newCol });
					visited[newRow][newCol] = true;
				}
			}
		}

		return null; // No tile found containing user unit
	}

//Determine whether the nearest location found is reasonable
	private static boolean isValid(int row, int col, int rows, int cols) {
		return row >= 0 && row < rows && col >= 0 && col < cols;
	}

	public Tile havaAdUnit(GameState gameState) {
		for (Tile cur : gameState.getUserPlayer().getAllUnitsAndTile().keySet()) {
			Unit userUnit = gameState.getAiPlayer().getUnitByTile(cur);
			Unit aiUnit = gameState.getAiPlayer().getAllUnitsAndTile().get(getClosestTile(cur, gameState));
			if (gameState.unitsAdjacent(aiUnit, userUnit)) {
				return cur;
			}
		}
		return null;
	}
}


