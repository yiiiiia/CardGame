package structures;
import java.util.List;
import java.util.ArrayList;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	// Game status flags
	public boolean gameInitialised = false;
	public boolean end = false;
	public boolean ignoreEvent = false;

	// Game entities
	public int playerMode; // 0 - human player; 1 - ai
	public Player player;
	public Player ai;
	public int turn; // Number of turns
	public Action pendingAction; // Action to perform after a unit stops
	public Unit activeUnit; // Currently selected unit

	// public Card activeCard; // Currently selected card

	// Collections to track game elements
	public List<Tile> gameTiles;
	public List<Unit> playerUnits;
	public List<Unit> aiUnits;
	/* 不知道这些attribute最后放哪个类，先注释了
	public List<Card> playerCardDeck;
	public List<Card> playerCardsAtHand;
	public List<Card> aiCardDeck;
	public List<Card> aiCardsAtHand;
	 */

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
	public Tile getTileByPos(int tilex, int tiley){
		for (Tilr tile : game Tiles) {
			if (tile.getTilex() == tilex && tile.getTileY() == tiley) {
				return tile;
			}
		}
		return null; //Tile not found
	}

	// Method to get the user player
	public Player getUserPlayer(){
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

	// Method to get all tiles
	public List<Tile> getAllTiles() {
		return gameTiles;
	}

	// Method to calculate tiles a unit can move to
	public List<Tile> tilesUnitCanMoveTo(Unit unit) {
		List<Tile> accessibleTiles = new ArrayList<>();
		// A method to calculate the range or reachable tiles
		int movementRange = 2; // Example range
		for (Tile tile : gameTiles) {
			if (Math.abs(tile.getTilex() - unit.getPosition().getTilex()) <= movementRange &&
					Math.abs(tile.getTiley() - unit.getPosition().getTiley()) <= movementRange) {
				accessibleTiles.add(tile);
			}
		}
		return accessibleTiles;
	}

	// Method to check if the game has ended
	public boolean checkEndCondition() {
		// Implement game end condition check
		return end;
	}

	// Method to handle player actions
	public void processPlayerAction(Action action) {
		// Process the action and update game state
		pendingAction = action;
		// Further action processing logic here
	}

	// Additional methods and logic as needed...
}
