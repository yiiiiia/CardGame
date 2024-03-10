package structures.basic;

import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * This is a representation of a Unit on the game board. A unit has a unique id
 * (this is used by the front-end. Each unit has a current UnitAnimationType,
 * e.g. move, or attack. The position is the physical position on the board.
 * UnitAnimationSet contains the underlying information about the animation
 * frames, while ImageCorrection has information for centering the unit on the
 * tile.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Unit {
	@JsonIgnore
	private static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java
																// objects from a file
	protected int id;
	protected String name;
	protected UnitAnimationType animation;
	protected Position position;
	protected UnitAnimationSet animations;
	protected ImageCorrection correction;
	protected int health;
	protected int maxHealth;
	protected int attack;
	protected boolean stunned;
	protected boolean hasMoved;
	protected boolean hasAttacked;
	protected boolean newlySpawned;
	protected int shieldBuff; // related with the effect of card Horn of the Forsaken

	public Unit() {
	}

	public void performAbility(AbilityType type, ActorRef out, GameState gameState) {
		throw new UnsupportedOperationException();
	}

	public List<AbilityType> getAbilityTypes() {
		throw new UnsupportedOperationException();
	}

	/**
	 * This command sets the position of the Unit to a specified tile.
	 * 
	 * @param tile
	 */
	@JsonIgnore
	public void setPositionByTile(Tile tile) {
		position = new Position(tile.getXpos(), tile.getYpos(), tile.getTilex(), tile.getTiley());
	}

	public boolean hasProvokeAbility() {
		for (AbilityType type : getAbilityTypes()) {
			if (type == AbilityType.PROVOKE) {
				return true;
			}
		}
		return false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UnitAnimationType getAnimation() {
		return animation;
	}

	public void setAnimation(UnitAnimationType animation) {
		this.animation = animation;
	}

	public ImageCorrection getCorrection() {
		return correction;
	}

	public void setCorrection(ImageCorrection correction) {
		this.correction = correction;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public UnitAnimationSet getAnimations() {
		return animations;
	}

	public void setAnimations(UnitAnimationSet animations) {
		this.animations = animations;
	}

	// New getters and setters for the added attributes
	public boolean isStunned() {
		return stunned;
	}

	public void setStunned(boolean stunned) {
		this.stunned = stunned;
	}

	public boolean getHasMoved() {
		return hasMoved;
	}

	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}

	public boolean getHasAttacked() {
		return hasAttacked;
	}

	public void setHasAttacked(boolean canAttack) {
		this.hasAttacked = canAttack;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public void incrAttack() {
		attack++;
	}

	public void incrHealth() {
		health++;
		if (health > maxHealth) {
			maxHealth = health;
		}
	}

	public void healSelf(int healPoint) {
		if (health + healPoint > maxHealth) {
			health = maxHealth;
		} else {
			health += healPoint;
		}
	}

	public int getShieldBuff() {
		return shieldBuff;
	}

	public void setShieldBuff(int artifactRobustness) {
		this.shieldBuff = artifactRobustness;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNewlySpawned() {
		return newlySpawned;
	}

	public void setNewlySpawned(boolean newlySpawned) {
		this.newlySpawned = newlySpawned;
	}

	// New method for unit movement
	public void move(ActorRef out, GameState gameState, Tile destination) {
		if (this.hasMoved) {
			throw new IllegalStateException("Cannot move: unit has already moved");
		}
		if (this.hasAttacked) {
			throw new IllegalStateException("Cannot move: unit has already attacked");
		}
		if (this.stunned) {
			throw new IllegalStateException("Cannot move: unit is stunned");
		}
		if (gameState.unitIsProvoked(this)) {
			throw new IllegalStateException("Cannot move: unit is provoked");
		}
		Set<Tile> accessibleTiles = gameState.getTilesUnitCanMoveTo(this);
		if (!accessibleTiles.contains(destination)) {
			throw new IllegalStateException("Cannot move: target tile out of range: " + destination);
		}
		boolean yfirst = false;
		int unitX = position.getTilex();
		int unitY = position.getTiley();
		int targetX = destination.getTilex();
		int targetY = destination.getTiley();
		if (unitY != targetY) {
			if (unitX == targetX) {
				yfirst = true;
			} else if (unitX < targetX) {
				Tile tile = gameState.getTileByPos(unitX + 1, unitY);
				if (tile.isOccupied()
						&& gameState.getPlayerUnitBelongsTo(this) != gameState.getPlayerUnitBelongsTo(tile.getUnit())) {
					yfirst = true;
				}
			} else {
				Tile tile = gameState.getTileByPos(unitX - 1, unitY);
				if (tile.isOccupied()
						&& gameState.getPlayerUnitBelongsTo(this) != gameState.getPlayerUnitBelongsTo(tile.getUnit())) {
					yfirst = true;
				}
			}
		}
		hasMoved = true;
		gameState.updateUnitPosition(this, destination);
		BasicCommands.moveUnitToTile(out, this, destination, yfirst);
	}

	public void doAttack(ActorRef out, GameState gameState, Unit attacked, boolean isCounterAttack) {
		if (!gameState.unitCanAttack(this, attacked) && !isCounterAttack) {
			throw new IllegalStateException("cannot perform attack");
		}
		if (!GameState.unitsAdjacent(this, attacked)) {
			throw new IllegalStateException("Cannot attack: target unit is out of range");
		}
		if (!isCounterAttack) {
			this.hasAttacked = true;
		}
		GameState.playUnitAnimation(out, this, UnitAnimationType.attack);
		GameState.playUnitAnimation(out, this, UnitAnimationType.idle);
		if (attacked.getShieldBuff() > 0) {
			BasicCommands.addPlayer1Notification(out,
					"User avatar is under buff protection: robutness " + attacked.getShieldBuff(), 3);
			BasicCommands.sleep(500);
		}
		GameState.playUnitAnimation(out, attacked, UnitAnimationType.hit);
		GameState.playUnitAnimation(out, attacked, UnitAnimationType.idle);
		boolean damageDone = gameState.dealDamangeToUnit(out, attacked, getAttack());
		if (damageDone && getShieldBuff() > 0) {
			Tile tile = gameState.getUnitTile(this);
			int gameMode = gameState.isUserUnit(this) ? GameState.USER_MODE : GameState.AI_MODE;
			gameState.summonWraithlingOnRandomlySelectedUnoccupiedAdjacentTile(out, tile, gameMode);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Unit other = (Unit) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Unit [id=" + id + ", name=" + name + ", health=" + health + ", maxHealth=" + maxHealth + ", attack="
				+ attack + ", shieldBuff=" + shieldBuff + "]";
	}
}
