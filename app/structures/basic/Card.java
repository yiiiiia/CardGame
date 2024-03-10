package structures.basic;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
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
	public static final int CAST_TO_ALLY = 1;
	public static final int CAST_TO_ENEMY = 2;

	protected int id;
	protected String cardname;
	protected int manacost;
	protected MiniCard miniCard;
	protected BigCard bigCard;
	protected boolean isCreature;
	protected String unitConfig;

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

	// ----------------------------------------------------------------------------------
	public void highlightTiles(ActorRef out, GameState gameState) {
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	public void castSpell(ActorRef out, GameState gameState, Tile tile) {
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	public String canCastSpellOnUnit(GameState gameState, Unit unit) {
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	@JsonIgnore
	public Class<? extends Unit> getSummonedCreatureClass() {
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	public void highlightTilesAsCreatureCard(ActorRef out, GameState gameState, int gameMode) {
		for (Tile tile : gameState.getTilesForSummon(gameMode)) {
			gameState.drawAndRecordHighlightedTile(out, tile, Tile.TILE_WHITE_MODE);
		}
	}

	// Method solely designed for car Wraithling Swarm,
	// because it requries user interaction
	public void delegateEventProcess(ActorRef out, GameState gameState, String messageType, JsonNode message) {
		throw new UnsupportedOperationException("Unimplemented method!");
	}

	public void summonUnitOnTile(ActorRef out, GameState gameState, Tile tile, int mode) {
		if (mode != GameState.USER_MODE && mode != GameState.AI_MODE) {
			throw new IllegalArgumentException("Invalid game mode: " + mode);
		}
		if (tile == null) {
			throw new IllegalArgumentException("tile is null");
		}
		if (mode == GameState.USER_MODE && manacost > gameState.getUserPlayer().getMana()) {
			throw new IllegalStateException("cannot use card due to lack of mana: " + this);
		} else if (mode == GameState.AI_MODE && manacost > gameState.getAiPlayer().getMana()) {
			throw new IllegalStateException("cannot use card due to lack of mana: " + this);
		}
		if (tile.isOccupied()) {
			throw new IllegalStateException("cannot summon unit on occupied tile!");
		}
		Set<Tile> tilesCanSummon = gameState.getTilesForSummon(mode);
		if (!tilesCanSummon.contains(tile)) {
			throw new IllegalStateException("Cannot summon creature, tile out of range");
		}
		Class<? extends Unit> clz = getSummonedCreatureClass();
		gameState.summonUnit(out, tile, clz, unitConfig, mode, false);
		gameState.deductManaFromPlayer(out, manacost, mode);
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
