package structures.basic;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Unit {
	@JsonIgnore
	private static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java
																// objects from a file
	protected int id;
	protected UnitAnimationType animation;
	protected Position position;
	protected UnitAnimationSet animations;
	protected ImageCorrection correction;

	// New attributes
	protected boolean stunned; // A stunned unit cannot move or attack in one turn (only AI)
	protected boolean hasMoved; // A unit can move only once per turn and cannot move after attacking
	protected boolean hasAttacked; // A unit can attack only once per turn
	protected boolean hasProvoke; // Some units has provoke ability
	protected int health;
	protected int attack;

	public Unit() {
	}

	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, int health, int attack) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;

		position = new Position(0, 0, 0, 0);
		this.correction = correction;
		this.animations = animations;
		this.health = health;
		this.attack = attack;
		this.stunned = false;
		this.hasMoved = false;
		this.hasAttacked = false;
		this.hasProvoke = false;
	}

	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile, int health,
			int attack) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;

		position = new Position(currentTile.getXpos(), currentTile.getYpos(), currentTile.getTilex(),
				currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
		this.health = health;
		this.attack = attack;
		this.stunned = false;
		this.hasMoved = false;
		this.hasAttacked = false;
		this.hasProvoke = false;
	}

	public Unit(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
			ImageCorrection correction, int health, int attack) {
		super();
		this.id = id;
		this.animation = animation;
		this.position = position;
		this.animations = animations;
		this.correction = correction;
		this.health = health;
		this.attack = attack;
		this.stunned = false;
		this.hasMoved = false;
		this.hasAttacked = false;
		this.hasProvoke = false;
	}

	/**
	 * This command sets the position of the Unit to a specified
	 * tile.
	 * 
	 * @param tile
	 */
	@JsonIgnore
	public void setPositionByTile(Tile tile) {
		position = new Position(tile.getXpos(), tile.getYpos(), tile.getTilex(), tile.getTiley());
	}

	// should be override by subclass
	public void performDeathWatch(ActorRef ref, GameState gameState) {
		throw new RuntimeException("Not implemented");
	}

	// should be override by subclass
	public void performGambit(ActorRef ref, GameState gameState) {
		throw new RuntimeException("Not implemented");
	}

	// should be override by subclass
	public void performDamageWatch(ActorRef ref, GameState gameState, Unit damagedUnit) {
		throw new RuntimeException("Not implemented");
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

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public boolean getHasProvoke() {
		return hasProvoke;
	}

	public void setHasProvoke(boolean hasProvoke) {
		this.hasProvoke = hasProvoke;
	}

	// New method for unit movement
	public void unitMove(ActorRef out, GameState gameState, Tile positionTile) {
		if (this.hasMoved) {
			throw new RuntimeException("Cannot move: unit has already moved");
		}
		if (this.hasAttacked) {
			throw new RuntimeException("Cannot move: unit has already moved");
		}
		if (this.stunned) {
			throw new RuntimeException("Cannot move: unit is stunned");
		}
		if (gameState.isUnitProvoked(this)) {
			throw new RuntimeException("Cannot move: unit is currently provoked");
		}
		// Check if the move is valid based on game rules
		List<Tile> accessibleTiles = gameState.getTilesUnitCanMoveTo(this);
		if (!accessibleTiles.contains(positionTile)) {
			throw new RuntimeException("Cannot move: target tile is out of range");
		}
		this.setPositionByTile(positionTile);
		this.hasMoved = true; // Prevent further movement this turn
		boolean yfirst = false;
		int unitX = this.position.getTilex();
		int unitY = this.position.getTiley();
		int targetX = positionTile.getTilex();
		int targetY = positionTile.getTiley();
		if (unitY != targetY) {
			if (unitX == targetX) {
				yfirst = true;
			} else if (unitX < targetX) {
				Tile tile = gameState.getTileByPos(unitX + 1, unitY);
				if (tile.getUnit() != null) {
					yfirst = true;
				}
			} else {
				Tile tile = gameState.getTileByPos(unitX - 1, unitY);
				if (tile.getUnit() != null) {
					yfirst = true;
				}
			}
		}
		BasicCommands.moveUnitToTile(out, this, positionTile, yfirst);
	}

	// New method for unit attack, considering attack range and movement
	// restrictions
	public void unitAttack(ActorRef out, GameState gameState, Unit targetUnit) {
		if (this.hasAttacked) {
			throw new RuntimeException("Cannot attack: unit has already attacked");
		}
		if (this.stunned) {
			throw new RuntimeException("Cannot attack: unit is stunned");
		}
		if (gameState.isUnitProvoked(this) && !targetUnit.getHasProvoke()) {
			throw new RuntimeException("Cannot attack: unit is provoked and can only attack provoke unit");
		}
		if (!GameState.unitsAdjacent(this, targetUnit)) {
			throw new RuntimeException("Cannot attack: target unit is out of range");
		}
		this.hasAttacked = true;
		GameState.displayUnitAttack(out, this, targetUnit);
		int newHealth = targetUnit.getHealth() - this.attack;
		targetUnit.setHealth(Math.max(newHealth, 0)); // Ensure health does not go below 0
		GameState.updateUnitHealth(out, targetUnit);
		gameState.triggerDamageEvents(out, targetUnit);
		if (targetUnit.getHealth() <= 0) {
			GameState.displayUnitDeath(out, targetUnit);
			gameState.removeUnit(out, targetUnit);
			gameState.triggerDamageEvents(out, targetUnit);
		}
	}
}
