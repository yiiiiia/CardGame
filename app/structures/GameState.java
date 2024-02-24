package structures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {
	public static final int ROWS = 5; // 5 rows
	public static final int COLS = 9; // 9 columns
	public static final int HUMAN_MODE = 0;
	public static final int AI_MODE = 1;

	// Game status flags
	private boolean gameInitialised = false;
	private boolean gameEnd = false;
	private boolean hasMovingUnit = false;
	private int playerMode; // 0 - human player; 1 - ai
	private int turn; // Number of turns

	// Collections to track game elements
	private Tile[][] gameTiles;
	private Player user;
	private Player ai;
	private Action pendingAction; // Action to perform after a unit stops
	private Unit activeUnit; // Currently selected unit
	private Card activateCard;
	private Unit userAvatar;
	private Unit aiAvatar;
	private List<Tile> provokeAreas; // A list of tiles where units on which will be provoked

    public void initGameTiles() {
        gameTiles = new Tile[COLS][ROWS];
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Tile tile = BasicObjectBuilders.loadTile(x, y);
                gameTiles[x][y] = tile;
            }
        }
    }

    public List<Tile> getGameTiles() {
        List<Tile> tiles = new ArrayList<>();
        if (gameTiles != null && gameTiles.length != 0) {
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    tiles.add(gameTiles[col][row]);
                }
            }
        }
        return tiles;
    }

	public Unit getUserAvatar() {
		return userAvatar;
	}

	public void setUserAvatar(Unit userAvatar) {
		this.userAvatar = userAvatar;
	}

	public Unit getAiAvatar() {
		return aiAvatar;
	}

	public void setAiAvatar(Unit aiAvatar) {
		this.aiAvatar = aiAvatar;
	}

	public Unit getUserAvatar() {
		return userAvatar;
	}

	public void setUserAvatar(Unit userAvatar) {
		this.userAvatar = userAvatar;
	}

	public Unit getAiAvatar() {
		return aiAvatar;
	}

	public void setAiAvatar(Unit aiAvatar) {
		this.aiAvatar = aiAvatar;
	}

	public void setGameInitialised(boolean initialised) {
		gameInitialised = initialised;
	}

	public boolean isGameInitialised() {
		return gameInitialised;
	}

	// Method to retrieve a Tile by its position
    public Tile getTileByPos(int tilex, int tiley) {
        if (tiley >= 0 && tiley < ROWS && tilex >= 0 && tilex < COLS) {
            return gameTiles[tilex][tiley];
        }
        return null;
    }

	// Method to get the user player
	public Player getUserPlayer() {
		return user;
	}

	public void setUserPlayer(Player user) {
		this.user = user;
	}

	// Method to get the AI player
	public Player getAiPlayer() {
		return ai;
	}

	public void setAiPlayer(Player ai) {
		this.ai = ai;
	}

	public boolean isGameEnd() {
		return gameEnd;
	}

	public void setGameEnd(boolean gameEnd) {
		this.gameEnd = gameEnd;
	}

	public boolean hasMovingUnit() {
		return hasMovingUnit;
	}

	public void setHasMovingUnit(boolean hasMovingUnit) {
		this.hasMovingUnit = hasMovingUnit;
	}

	public int getPlayerMode() {
		return playerMode;
	}

	public void setPlayerMode(int playerMode) {
		this.playerMode = playerMode;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public Action getPendingAction() {
		return pendingAction;
	}

	public void setPendingAction(Action action) {
		this.pendingAction = action;
	}

	public Card getActivateCard() {
		return activateCard;
	}

	public void setActivateCard(Card activateCard) {
		this.activateCard = activateCard;
	}

	public void setActiveUnit(Unit unit) {
		this.activeUnit = unit;
	}

	public Unit getActiveUnit() {
		return activeUnit;
	}

	public void clearActiveCard() {
		this.activateCard = null;
	}

	public void clearActiveUnit() {
		this.activeUnit = null;
	}

	public List<Tile> getProvokeAeras() {
		return provokeAreas;
	}

	public void setProvokeAeras(List<Tile> provokeAreas) {
		this.provokeAreas = provokeAreas;
	}

	public void applyStun(Unit unit) {
		unit.setStunned(true);
	}

	// Method to calculate tiles a unit can move to
	public List<Tile> getTilesUnitCanMoveTo(Unit unit) {
		List<Tile> accessibleTiles = new ArrayList<>();
		int unitTileX = unit.getPosition().getTilex();
		int unitTileY = unit.getPosition().getTiley();
		Tile unitTile = getTileByPos(unitTileX, unitTileY);

		for (Tile targetTile : getGameTiles()) {
			if (targetTile.getUnit() != null) {
				// tile already occupied
				continue;
			}
			int tileX = targetTile.getTilex();
			int tileY = targetTile.getTiley();
			int diffX = Math.abs(tileX - unitTileX);
			int diffY = Math.abs(tileY - unitTileY);
			// Determine if the target Tile is within moving range
			boolean inRange = (diffX <= 2 && tileY == unitTileY) || (diffY <= 2 && tileX == unitTileX)
					|| (diffX == 1 && diffY == 1);
			if (!inRange) {
				continue;
			}
			// Check that the Tile is not occupied and that the path is not blocked
			if (isPathBlocked(unitTile, targetTile)) {
				continue;
			}
			accessibleTiles.add(targetTile);
		}
		return accessibleTiles;
	}

	// Check that the path from start to end is not blocked
	private boolean isPathBlocked(Tile startTile, Tile endTile) {
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
			Tile tile = getTileByPos(currentX, currentY);
			if (tile.getUnit() != null) {
				return true;
			}
		}
		return false;
	}

	private boolean checkDiagonalPathBlocked(Tile startTile, Tile endTile, int stepX, int stepY) {
		int currentX = startTile.getTilex();
		int currentY = startTile.getTiley();
		while (currentX != endTile.getTilex() && currentY != endTile.getTiley()) {
			Tile tile = getTileByPos(currentX + stepX, currentY);
			if (tile.getUnit() != null) {
				return true;
			}
			tile = getTileByPos(currentX, currentY + stepY);
			if (tile.getUnit() != null) {
				return true;
			}
			currentX += stepX;
			currentY += stepY;
		}
		return false;
	}

	public void moveUnitToTile(ActorRef out, Unit unit, Tile tile) {
		BasicCommands.moveUnitToTile(out, unit, tile);
	}

	public List<Tile> getTilesWithEnemyUnitsInRange(Unit unit, int userMode) {
		Set<Tile> set = new HashSet<>();
		List<Tile> accessibleTiles = getTilesUnitCanMoveTo(unit);
		Tile unitTile = getTileByPos(unit.getPosition().getTilex(), unit.getPosition().getTiley());
		accessibleTiles.add(unitTile);
		for (Tile t : accessibleTiles) {
			List<Tile> tilesNearby = getAdjacentTiles(t);
			for (Tile nearby : tilesNearby) {
				if (nearby.getUnit() != null) {
					if (userMode == HUMAN_MODE && isAiUnit(nearby.getUnit())) {
						set.add(nearby);
					} else if (userMode == AI_MODE && isUserUnit(nearby.getUnit())) {
						set.add(nearby);
					}
				}
			}
		}
		return new ArrayList<>(set);
	}

	public List<Tile> getAdjacentTiles(Tile tile) {
		List<Tile> result = new ArrayList<>();
		List<Tile> allTiles = getGameTiles();
		for (Tile t : allTiles) {
			if (tilesAdjacent(tile, t)) {
				result.add(t);
			}
		}
		return result;
	}

	public boolean isUserUnit(Unit unit) {
		return user.hasUnit(unit);
	}

	public boolean isAiUnit(Unit unit) {
		return ai.hasUnit(unit);
	}

	// Trigger all units' abilities related with death
	public void triggerDeathEvents(ActorRef out) {
		for (Unit unit : this.user.getAllUnits()) {
			unit.performDeathWatch(out, this);
		}
		for (Unit unit : this.ai.getAllUnits()) {
			unit.performDeathWatch(out, this);
		}
	}

	// Trigger all units' abilities related with a unit being summoned
	public void triggerSummonEvents(ActorRef out) {
		for (Unit unit : this.user.getAllUnits()) {
			unit.performGambit(out, this);
		}
		for (Unit unit : this.ai.getAllUnits()) {
			unit.performGambit(out, this);
		}
	}

	// Trigger all units' abilities related with a unit being damaged
	public void triggerDamageEvents(ActorRef out, Unit damaagedUnit) {
		for (Unit unit : this.user.getAllUnits()) {
			unit.performDamageWatch(out, this, damaagedUnit);
		}
		for (Unit unit : this.ai.getAllUnits()) {
			unit.performDamageWatch(out, this, damaagedUnit);
		}
	}

	public void removeUnit(ActorRef out, Unit unit) {
		if (isUserUnit(unit)) {
			user.removeUnit(unit);
		} else {
			ai.removeUnit(unit);
		}
		BasicCommands.deleteUnit(out, unit);
	}

	public boolean isUnitProvoked(Unit unit) {
		Position pos = unit.getPosition();
		for (Tile tile : provokeAreas) {
			if (tile.getTilex() == pos.getTilex() && tile.getTiley() == pos.getTiley()) {
				return true;
			}
		}
		return false;
	}

	public boolean unitCanMove(Unit unit) {
		if (unit.getHasMoved()) {
			return false;
		}
		if (unit.getHasAttacked()) {
			return false;
		}
		if (unit.isStunned()) {
			return false;
		}
		if (isUnitProvoked(unit)) {
			return false;
		}
		return true;
	}

	public boolean unitCanAttack(Unit attacker, Unit attacked) {
		if (attacker.getHasAttacked()) {
			return false;
		}
		if (attacker.isStunned()) {
			return false;
		}
		if (attacked != null) {
			if (isUnitProvoked(attacker) && !attacked.getHasProvoke()) {
				return false;
			}
		}
		return true;
	}

	public static void displayUnitAttack(ActorRef out, Unit attacker, Unit target) {
		int n = BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
		BasicCommands.playUnitAnimation(out, target, UnitAnimationType.hit);
		// wait for attack effect to finish
		sleepMilliseconds(n);
		BasicCommands.playUnitAnimation(out, target, UnitAnimationType.idle);
	}

	public static void displayUnitDeath(ActorRef out, Unit unit) {
		int n = BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.death);
		sleepMilliseconds(n);
	}

	public static void updateUnitHealth(ActorRef out, Unit unit) {
		BasicCommands.setUnitHealth(out, unit, unit.getHealth());
	}

	public static boolean unitsAdjacent(Unit u1, Unit u2) {
		int u1x = u1.getPosition().getTilex();
		int u1y = u1.getPosition().getTiley();
		int u2x = u2.getPosition().getTilex();
		int u2y = u2.getPosition().getTiley();
		int diffX = Math.abs(u1x - u2x);
		int diffY = Math.abs(u1y - u2y);
		return diffX <= 1 && diffY <= 1;
	}

	public static boolean tilesAdjacent(Tile t1, Tile t2) {
		int t1x = t1.getTilex();
		int t1y = t1.getTiley();
		int t2x = t2.getTilex();
		int t2y = t2.getTiley();
		int diffX = Math.abs(t1x - t2x);
		int diffY = Math.abs(t1y - t2y);
		return diffX <= 1 && diffY <= 1;
	}

	public static boolean isValidPosition(int tileX, int tileY) {
		return tileX >= 0 && tileX < COLS && tileY >= 0 && tileY < ROWS;
	}

	public static void sleepMilliseconds(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public  void PlaceableArea(ActorRef out,Tile tile,int is)
	{int x=tile.getTilex()-1;
		int y=tile.getTiley()-1;
		for(int i=0;i<3;i++)
	{for(int j=0;j<3;j++)
		{if(isPlaceBle(x+i,y+j))
		{
			BasicCommands.drawTile(out, this.getTileByPos(x+i,y+j), is);
		}
		
		}}
	
	}
	
	public boolean isPlaceBle(int x,int y)
	{if(this.getUserPlayer().getAllUnitsAndTile().containsKey(this.getTileByPos(x,y))||x<0||x>8||y<0||y>4||this.getUserPlayer().getAllUnitsAndTile().containsKey(this.getTileByPos(x,y)))
	{return false;}
	return true;
	}

}

