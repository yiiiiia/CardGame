package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import commands.BasicCommands;
import structures.GameState;
import akka.actor.ActorRef;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	private int id;
	private UnitAnimationType animation;
	private Position position;
	private UnitAnimationSet animations;
	private ImageCorrection correction;
	// New attributes
	private boolean stunned; // A stunned unit cannot move or attack in one turn (only AI)
	private boolean canMove; // A unit can move only once per turn and cannot move after attacking
	private int health;
	private int attack;

	public Unit() {}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, int health, int attack) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(0,0,0,0);
		this.correction = correction;
		this.animations = animations;
		this.health = health;
		this.attack = attack;
		this.stunned = false;
		this.canMove = true;
	}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile, int health, int attack) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(currentTile.getXpos(),currentTile.getYpos(),currentTile.getTilex(),currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
		this.health = health;
		this.attack = attack;
		this.stunned = false;
		this.canMove = true;
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
		this.canMove = true;
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
	public boolean isStunned(){
		return stunned;
	}
	public void setStunned(boolean stunned){
		this.stunned = stunned;
	}

	public boolean canMove(){
		return canMove;
	}
	public void setCanMove(boolean canMove){
		this.canMove = canMove;
	}

	public int getHealth(){
		return health;
	}
	public void setHealth(int health){
		this.health = health;
	}

	public int getAttack(){
		return attack;
	}
	public void setAttack(int attack){
		this.attack = attack;
	}

	// Method to play specific animation
	private void playAnimation(UnitAnimationType animationType){
		UnitAnimation animation = this.animations.getAnimationByType(animationType);
		if(animation != null){
			// A method in the game engine to play animations
		}
		System.out.println("Playing animation: " + animationType);
	} else {
		System.out.println("Animation for type " + animationType + " not found.");
	}
}

	public void animate(UnitAnimationType type){
		this.animation = type;
		if (type == UnitAnimationType.move) {
			playAnimation(this.animations.getMove());
		}
	}
	 public UnitAnimation getAnimationByType(UnitAnimationType animationType){

	 }
	// New method for unit movement
	public void unitMoving(GameState gameState, Tile positionTile) {
		if (this.canMove && !this.stunned) {
			// Check if the move is valid based on game rules
			List<Tile> accessibleTiles = gameState.tilesUnitCanMoveTo(this);
			if (accessibleTiles.contains(positionTile)) {
				this.setPositionByTile(positionTile);
				this.canMove = false; // Prevent further movement this turn
				this.animation = UnitAnimationType.move;
				BasicCommands.moveUnitToTile(gameState.getOut(), this, positionTile);
				BasicCommands.playUnitAnimation(gameState.getOut(), this, UnitAnimationType.move);
			}
		}
	}
	// New method for unit attack, considering attack range and movement restrictions
	public void unitAttack(GameState gameState, Unit targetUnit) {
		if (!this.stunned && this.canMove) {
			List<Unit> attackableUnits = gameState.unitsWithinAttackRange(this);
			if (attackableUnits.contains(targetUnit)) {
				int newHealth = targetUnit.getHealth() - this.attack;
				targetUnit.setHealth(Math.max(newHealth, 0)); // Ensure health does not go below 0

				if (targetUnit.getHealth() <= 0) {
					gameState.removeUnit(targetUnit);
					// You may need to implement or call removeUnit method in GameState
					BasicCommands.playUnitAnimation(gameState.getOut(), targetUnit, UnitAnimationType.death);
				} else {
					// If the target unit survives, it should not move in this turn
					targetUnit.setCanMove(false);
				}

				this.canMove = false; // This unit cannot move after attacking
				BasicCommands.playUnitAnimation(gameState.getOut(), this, UnitAnimationType.attack);
			}
		}
	}

	public void highlightAttackOptions(GameState gameState) {
		Set<Unit> enemiesInRange = new HashSet<>();
		Set<Tile> highlightedTiles = new HashSet<>();

		// Directly attackable enemies
		List<Unit> directlyAttackable = gameState.unitsWithinAttackRange(this);
		enemiesInRange.addAll(directlyAttackable);

		// Potential enemies attackable after moving
		List<Tile> accessibleTiles = gameState.tilesUnitCanMoveTo(this);
		for (Tile tile : accessibleTiles) {
			// Simulate attack range from each accessible tile without actually moving the unit
			List<Unit> attackableAfterMove = gameState.simulatedUnitsWithinAttackRange(this, tile);
			enemiesInRange.addAll(attackableAfterMove);
		}

		// Highlight tiles for directly attackable enemies and those attackable after simulated move
		for (Unit enemy : enemiesInRange) {
			Tile enemyTile = gameState.getTileByPos(enemy.getPosition().getTilex(), enemy.getPosition().getTiley());
			highlightedTiles.add(enemyTile);
		}

		// Use game engine's method to highlight tiles (assuming BasicCommands.drawTile for demonstration)
		for (Tile tile : highlightedTiles) {
			BasicCommands.drawTile(gameState.getOut(), tile, 2); // Assuming mode 2 is for highlighting
		}
	}




	/**
	 * This command sets the position of the Unit to a specified
	 * tile.
	 * @param tile
	 */
	@JsonIgnore
	public void setPositionByTile(Tile tile) {
		position = new Position(tile.getXpos(),tile.getYpos(),tile.getTilex(),tile.getTiley());
	}
	
	
}
