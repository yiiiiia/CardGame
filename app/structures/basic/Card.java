package structures.basic;

import akka.actor.ActorRef;
import structures.GameState;

/**
 * This is the base representation of a Card which is rendered in the player's hand.
 * A card has an id, a name (cardname) and a manacost. A card then has a large and mini
 * version. The mini version is what is rendered at the bottom of the screen. The big
 * version is what is rendered when the player clicks on a card in their hand.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Card {
	public static final int APPLY_NO_UNIT = 0; // cannot apply to any unit
	public static final int APPLY_ENEMY_UNIT = 0; // can only apply to enemy unit
	public static final int APPLY_ALLY_UNIT = 0; // can only apply to ally unit
	public static final int CARD_NORMAL_MODE = 0;
	public static final int CARD_ACTIVE_MODE = 1;

	private int id;
	private String cardname;
	private int manacost;
	private MiniCard miniCard;
	private BigCard bigCard;
	private boolean isCreature;
	private String unitConfig;
	private int applyOnUnitType;
	
	public Card() {};
	
	public Card(int id, String cardname, int manacost, MiniCard miniCard, BigCard bigCard, boolean isCreature, String unitConfig) {
		super();
		this.id = id;
		this.cardname = cardname;
		this.manacost = manacost;
		this.miniCard = miniCard;
		this.bigCard = bigCard;
		this.isCreature = isCreature;
		this.unitConfig = unitConfig;
	}

	// TODO implementation
	// perform spell cast on the unit
	public void castSpell(ActorRef out, GameState gameState, Unit unit) {
		throw new RuntimeException("should be override by sub-class");
	}

	// TODO implementation
	// summon unit on the tile
	public void summonUnit(ActorRef out, GameState gameState, Tile tile) {
		throw new RuntimeException("should be override by sub-class");
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
	public boolean getIsCreature() {
		return isCreature;
	}
	public void setIsCreature(boolean isCreature) {
		this.isCreature = isCreature;
	}
	public void setCreature(boolean isCreature) {
		this.isCreature = isCreature;
	}
	public boolean isCreature() {
		return isCreature;
	}
	public String getUnitConfig() {
		return unitConfig;
	}
	public void setUnitConfig(String unitConfig) {
		this.unitConfig = unitConfig;
	}

	public int getApplyOnUnitType() {
		return applyOnUnitType;
	}

	public void setApplyOnUnitType(int applyOnUnitType) {
		this.applyOnUnitType = applyOnUnitType;
	}
}
