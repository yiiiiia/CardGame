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
	private boolean gameInitialised = false;
	private boolean something = false;
	private boolean end = false;
	private boolean ignoreEvent = false;

	// Game entities
	private int playerMode; // 0 - human player; 1 - ai
	private Player player;
	private Player ai;
	private int turn; // Number of turns
	private Action pendingAction; // Action to perform after a unit stops
	private Unit activeUnit; // Currently selected unit

	// 这个变量也是 public Card activeCard; // Currently selected card

	// Collections to track game elements
	private List<Tile> gameTiles;
	private List<Unit> playerUnits;
	private List<Unit> aiUnits;
	/* 不知道这些变量最后放哪个类，先注释了
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
		for (Tilr tile : gameTiles) {
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
		int unitTileX = unit.getPosition().getTilex();
		int unitTileY = unit.getPosition().getTiley();
		// Check tiles within 2 steps in any 4 cardinal directions and 1 step diagonally
		for (Tile tile : gameTiles) {
			int tileX = tile.getTilex();
			int tileY = tile.getTiley();
			int diffX = Math.abs(tileX- unitTileX);
			int diffY = Math.abs(tileY - unitTileY);
			if ((diffX <= 2 && tileY == unitTileY) || (diffY <= 2 && tileX == unitTileX) ||
					(diffX == 1 && diffY == 1)){
				accessibleTile.add(tile);
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
