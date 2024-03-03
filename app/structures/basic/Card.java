package structures.basic;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * This is the base representation of a Card which is rendered in the player's
 * hand. A card has an id, a name (cardname) and a manacost. A card then has a
 * large and mini version. The mini version is what is rendered at the bottom of
 * the screen. The big version is what is rendered when the player clicks on a
 * card in their hand.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Card {
	public static final int CARD_NORMAL_MODE = 0;
	public static final int CARD_ACTIVE_MODE = 1;

	protected int id;
	protected String cardname;
	protected int manacost;
	protected MiniCard miniCard;
	protected BigCard bigCard;
	protected boolean isCreature;
	protected String unitConfig;
	protected boolean used;

	public Card() {
	};

	public Card(int id, String cardname, int manacost, MiniCard miniCard, BigCard bigCard, boolean isCreature,
			String unitConfig) {
		this.id = id;
		this.cardname = cardname;
		this.manacost = manacost;
		this.miniCard = miniCard;
		this.bigCard = bigCard;
		this.isCreature = isCreature;
		this.unitConfig = unitConfig;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCardname() {
		return cardname;
	}

	public void setCardname(String cardname) {
		this.cardname = cardname;
	}

	public int getManacost() {
		return manacost;
	}

	public void setManacost(int manacost) {
		this.manacost = manacost;
	}

	public MiniCard getMiniCard() {
		return miniCard;
	}

	public void setMiniCard(MiniCard miniCard) {
		this.miniCard = miniCard;
	}

	public BigCard getBigCard() {
		return bigCard;
	}

	public void setBigCard(BigCard bigCard) {
		this.bigCard = bigCard;
	}

	public String getUnitConfig() {
		return unitConfig;
	}

	public void setUnitConfig(String unitConfig) {
		this.unitConfig = unitConfig;
	}

	public boolean getIsCreature() {
		return isCreature;
	}

	public void setIsCreature(boolean isCreature) {
		this.isCreature = isCreature;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public void highlightTiles(ActorRef out, GameState gameState) {
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	public void castSpell(ActorRef out, GameState gameState, Tile tile) {
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	protected Class<? extends Unit> getSummonedCreatureClass() {
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	// Method solely designed for car Wraithling Swarm,
	// because it requries user interaction
	public void delegateEventProcess(ActorRef out, GameState gameState, String messageType, JsonNode message) {
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	public void summonUnitOnTile(ActorRef out, GameState gameState, Tile tile, int mode) {
		if (mode == GameState.USER_MODE) {
			if (manacost > gameState.getUserPlayer().getMana()) {
				BasicCommands.addPlayer1Notification(out, "Not enough mana", 3);
				return;
			}
		} else if (mode == GameState.AI_MODE) {
			if (manacost > gameState.getAiPlayer().getMana()) {
				BasicCommands.addPlayer1Notification(out, "FOR AI: Not enough mana", 3);
				return;
			}
		} else {
			throw new IllegalArgumentException("Invalid mode: " + mode);
		}
		if (!isCreature) {
			throw new IllegalStateException("Card cannot summon unit!");
		}
		if (tile == null) {
			throw new IllegalArgumentException("Tile is null");
		}
		if (tile.isOccupied()) {
			throw new IllegalStateException("cannot summon unit on occupied tile!");
		}
		List<Tile> tilesCanSummon = gameState.getTilesForSummon(mode);
		if (!tilesCanSummon.contains(tile)) {
			BasicCommands.addPlayer1Notification(out, "Cannot summon creature, tile out of range", 5);
			return;
		}
		Class<? extends Unit> clz = getSummonedCreatureClass();
		gameState.summonUnit(out, tile, clz, unitConfig, mode, false);
		gameState.deductManaFromPlayer(out, manacost, mode);
		setUsed(true); // mark card used
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((cardname == null) ? 0 : cardname.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card other = (Card) obj;
		if (id != other.id)
			return false;
		if (cardname == null) {
			if (other.cardname != null)
				return false;
		} else if (!cardname.equals(other.cardname))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Card [id=" + id + ", cardname=" + cardname + ", manacost=" + manacost + "]";
	}
}
