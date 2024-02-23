package structures;
import java.util.List;
import java.util.ArrayList;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import akka.actor.ActorRef;
import commands.BasicCommands;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	// Game status flags
	public static final int rows = 5; // 5 rows
	public static final int cols = 9; // 9 columns
	private boolean gameInitialised = false;
	private boolean end = false;
	private boolean ignoreEvent = false;
	private ActorRef out;

	// Game entities
	private int playerMode; // 0 - human player; 1 - ai
	private Player player;
	private Player ai;
	private int turn; // Number of turns
	private Action pendingAction; // Action to perform after a unit stops
	private Unit activeUnit; // Currently selected unit
	private Card activateCard;

	// Collections to track game elements
	private List<Tile> gameTiles;
	private Tile[][] gameTiles;


	public GameState() {
		gameTiles = new ArrayList<>();
		playerUnits = new ArrayList<>();
		aiUnits = new ArrayList<>();
		 // Dimensions of the game board
		 gameTiles = new Tile[rows][cols];
		 // Initialize each Tile object
		 for (int i = 0; i < rows; i++) {
			 for (int j = 0; j < cols; j++) {
				 gameTiles[i][j] = new Tile(j, i);
				 }
				 }
				 }

	// Example method to initialize game state
	public void initializeGame() {
		// Initialize tiles, units, players, etc.
		gameInitialised = true;
		// Further initialization logic here
	}

	// Method to update the game state on each turn
	public void updateTurn() {
		turn++;
		// Additional logic to handle turn update
	}

	// Method to retrieve a Tile by its position
	public Tile getTileByPos(int tilex, int tiley) {
    if (tiley >= 0 && tiley < gameTiles.length && 
	tilex >= 0 && tilex < gameTiles[tiley].length) {
        return gameTiles[tiley][tilex];
    }
    return null; // Tile not found or out of bounds
}

	// Method to get the user player
	public Player getUserPlayer() {
		return player;
	}

	// Method to get the AI player
	public Player getAiPlayer() {
		return ai;
	}

	// Method to get the currently active unit
	public Unit getActiveUnit() {
		return activeUnit;
	}

	// Method to set the active unit
	public void setActiveUnit(Unit unit) {
		this.activeUnit = unit;
	}

	public Card getActiveCard() {
        return activeCard;
    }
	
	public void setActiveCard(Card activeCard) {
        this.activeCard = activeCard;
    }

	// Method to get all tiles
	public List<Tile> getAllTiles() {
		return gameTiles;
	}

	// Method to calculate tiles a unit can move to
	public List<Tile> tilesUnitCanMoveTo(Unit unit) {
    List<Tile> accessibleTiles = new ArrayList<>();
    int unitTileX = unit.getPosition().getTilex();
    int unitTileY = unit.getPosition().getTiley();

    for (int i = 0; i < gameTiles.length; i++) {
        for (int j = 0; j < gameTiles[i].length; j++) {
            Tile tile = gameTiles[i][j];
            int tileX = tile.getTilex();
            int tileY = tile.getTiley();
            int diffX = Math.abs(tileX - unitTileX);
            int diffY = Math.abs(tileY - unitTileY);

            // Determine if the target Tile is within moving range
            boolean inRange = (diffX <= 2 && tileY == unitTileY) || (diffY <= 2 && tileX == unitTileX) || (diffX == 1 && diffY == 1);

            // Check that the Tile is not occupied and that the path is not blocked
            if (inRange && !isTileOccupied(tile) && !isPathBlocked(gameTiles[unitTileY][unitTileX], tile)) {
                accessibleTiles.add(tile);
            }
        }
    }

    return accessibleTiles;
}

// Checks if the specified Tile is occupied by any units
public boolean isTileOccupied(Tile tile) {
    for (Unit unit : getAllUnits()) {
        if (unit.getPosition().getTilex() == tile.getTilex() && unit.getPosition().getTiley() == tile.getTiley()) {
            return true; // Find a unit occupying this Tile
        }
    }
    return false; // No units occupy this Tile
}

// Check that the path from start to end is not blocked
public boolean isPathBlocked(Tile startTile, Tile endTile) {
    // Calculate the difference between the starting point and the end point
    int deltaX = endTile.getTilex() - startTile.getTilex();
    int deltaY = endTile.getTiley() - startTile.getTiley();

    // Determining the direction of movement
    int stepX = Integer.signum(deltaX);
    int stepY = Integer.signum(deltaY);

    // Handling of cardinal paths
    if (deltaX == 0 || deltaY == 0) {
        return checkCardinalPathBlocked(startTile, endTile, stepX, stepY);
    } else { // For diagonal paths
        return checkDiagonalPathBlocked(startTile, endTile, stepX, stepY);
    }
}

private boolean checkCardinalPathBlocked(Tile startTile, Tile endTile, int stepX, int stepY) {
    int currentX = startTile.getTilex();
    int currentY = startTile.getTiley();
    while (currentX != endTile.getTilex() || currentY != endTile.getTiley()) {
        currentX += stepX;
        currentY += stepY;
        // Check if the current Tile is blocked
        if (isTileOccupied(findTileByPosition(currentX, currentY))) {
            return true;
        }
    }
    return false;
}

private boolean checkDiagonalPathBlocked(Tile startTile, Tile endTile, int stepX, int stepY) {
    int currentX = startTile.getTilex();
    int currentY = startTile.getTiley();
    while ((currentX != endTile.getTilex()) && (currentY != endTile.getTiley())) {
        if (isTileOccupied(findTileByPosition(currentX + stepX, currentY)) || 
		isTileOccupied(findTileByPosition(currentX, currentY + stepY))) {
            return true;
        }
        currentX += stepX;
        currentY += stepY;
    }
    return false;
}


	private boolean isUnitInRange(Unit attacker, Unit target) {
		int attackerTileX = attacker.getPosition().getTilex();
		int attackerTileY = attacker.getPosition().getTiley();
		int targetTileX = target.getPosition().getTilex();
		int targetTileY = target.getPosition().getTiley();

		return Math.abs(attackerTileX - targetTileX) <= 1
				&& Math.abs(attackerTileY - targetTileY) <= 1;
	}

	// Method to combine player and AI units for range checking
	private List<Unit> getAllUnits() {
		List<Unit> allUnits = new ArrayList<>();
		allUnits.addAll(playerUnits);
		allUnits.addAll(aiUnits);
		return allUnits;
	}

	// Method to get units within attack range of a given unit
	public List<Unit> unitsWithinAttackRange(Unit attacker) {
		List<Unit> attackableUnits = new ArrayList<>();
		// Assuming attack range is 1 tile around the attacker for simplicity
		for (Unit unit : getAllUnits()) {
			if (isUnitInRange(attacker, unit)) {
				attackableUnits.add(unit);
			}
		}
		return attackableUnits;
	}

	public List<Unit> simulatedUnitsWithinAttackRange(Unit unit, Tile simulatedTile) {
		List<Unit> unitsWithinRange = new ArrayList<>();

		// Get a list of all units, both player and AI units
		List<Unit> allUnits = new ArrayList<>();
		allUnits.addAll(playerUnits);
		allUnits.addAll(aiUnits);

		for (Unit potentialTarget : allUnits) {
			if (potentialTarget == unit) continue; // skip over itself

			// Calculation of cardinal and diagonal distances between potential target units and simulated tiles
			int deltaX = Math.abs(potentialTarget.getPosition().getTilex() - simulatedTile.getTilex());
			int deltaY = Math.abs(potentialTarget.getPosition().getTiley() - simulatedTile.getTiley());
			int distance = Math.max(deltaX, deltaY);

			// Determine whether a potential target is within range of an attack
			if ((distance == 1 && deltaX + deltaY == 1) ||
					(distance <= 2 && deltaX + deltaY != 2)) {
				unitsWithinRange.add(potentialTarget);
			}
		}

		return unitsWithinRange;
	}

	public void moveUnitToTile(Unit unit, Tile tile) {
		BasicCommands.moveUnitToTile(out, unit, tile);
	}

	public boolean isUnitWithinAttackRange(Unit attacker, Unit target) {
		// Implement attack range logic
		return unitsWithinAttackRange(attacker).contains(target);
	}

	public void applyStun(Unit unit) {
		unit.setStunned(true);
	}

	public void attackUnit(Unit attacker, Unit target) {
		int newHealth = target.getHealth() - attacker.getAttack();
		target.setHealth(newHealth);
		updateUnitHealth(target);
		if (newHealth <= 0) {
			removeUnit(target); // Remove from the board
		} else {
			counterAttack(target, attacker); // Counterattack if alive
		}
	}

	private void counterAttack(Unit defender, Unit attacker) {
		if (defender.getHealth() > 0) {
			int newHealth = attacker.getHealth() - defender.getAttack();
			attacker.setHealth(newHealth);
			updateUnitHealth(attacker);
			if (newHealth <= 0) {
				removeUnit(attacker); // Remove attacker if health drops to 0
			}
		}
	}

	// Add method to trigger update of unit animation
	public void triggerUnitAnimation(Unit unit, UnitAnimationType animationType) {
		BasicCommands.playUnitAnimation(out, unit, animationType);
	}

	public void updateUnitHealth(Unit unit) {
		BasicCommands.setUnitHealth(out, unit, unit.getHealth());
	}

	public void removeUnit(Unit unit) {
		playerUnits.remove(unit);
		aiUnits.remove(unit);
		BasicCommands.deleteUnit(out, unit);
	}




	// Method to check if the game has ended
	public boolean checkEndCondition() {
		// Implement game end condition check
		return end;
	}

	// Method to handle player actions
	public void setPendingAction(Action action) {
    this.pendingAction = action;
}

	// Additional methods and logic as needed...
}
