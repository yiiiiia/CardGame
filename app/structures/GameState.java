package structures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.AbilityType;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.basic.unit.SaberspineTiger;
import structures.basic.unit.Wraithling;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * This class can be used to hold information about the on-going game. Its
 * created with the GameActor.
 *
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {
	public static final int ROWS = 5; // 5 rows
	public static final int COLS = 9; // 9 columns
	public static final int USER_MODE = 0;
	public static final int AI_MODE = 1;
	public static final int END_GAME_MODE = 2;
	private static final Random rand = new Random();
	private static int unitIdCounter = 0;
	private static int cardIdCounter = 0;

	// Game status flags
	private boolean gameInitialised = false;
	private boolean gameOver = false;
	private boolean hasMovingUnit = false;
	private int gameMode; // 0 - human player; 1 - ai
	private int turn; // Number of turns

	// Collections to track game elements
	private Tile[][] gameTiles;
	private Player userPlayer;
	private Player aiPlayer;
	private Action pendingAction; // Action to perform after a unit stops
	private Unit activeUnit; // Currently selected unit
	private Card activeCard;
	private Unit userAvatar;
	private Unit aiAvatar;
	private int activateCardPos = -1;
	// areas that ai units will be provoked
	private Set<Tile> userProvokeAreas = new HashSet<>();
	// areas that user units will be provoked
	private Set<Tile> aiProvokeAreas = new HashSet<>();
	private Card delegatedCard; // delegated card to hand click event

	public boolean isGameInitialised() {
		return gameInitialised;
	}

	public int getActivateCardPos() {
		return this.activateCardPos;
	}

	public void setActivateCardPos(int a) {
		this.activateCardPos = a;
	}

	public void setGameInitialised(boolean gameInitialised) {
		this.gameInitialised = gameInitialised;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameEnd) {
		this.gameOver = gameEnd;
	}

	public boolean hasMovingUnit() {
		return hasMovingUnit;
	}

	public void setHasMovingUnit(boolean hasMovingUnit) {
		this.hasMovingUnit = hasMovingUnit;
	}

	public int getGameMode() {
		return gameMode;
	}

	public void setGameMode(int playerMode) {
		this.gameMode = playerMode;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public void setGameTiles(Tile[][] gameTiles) {
		this.gameTiles = gameTiles;
	}

	public Player getUserPlayer() {
		return userPlayer;
	}

	public void setUserPlayer(Player userPlayer) {
		this.userPlayer = userPlayer;
	}

	public Player getAiPlayer() {
		return aiPlayer;
	}

	public void setAiPlayer(Player aiPlayer) {
		this.aiPlayer = aiPlayer;
	}

	public Action getPendingAction() {
		return pendingAction;
	}

	public void setPendingAction(Action pendingAction) {
		this.pendingAction = pendingAction;
	}

	public Unit getActiveUnit() {
		return activeUnit;
	}

	public void setActiveUnit(Unit activeUnit) {
		this.activeUnit = activeUnit;
	}

	public Card getActiveCard() {
		return activeCard;
	}

	public void setActiveCard(ActorRef out, Card activateCard, int handPos) {
		this.activeCard = activateCard;
		BasicCommands.drawCard(out, activateCard, handPos, Card.CARD_ACTIVE_MODE);
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

	public Card getDelegatedCard() {
		return delegatedCard;
	}

	public void setDelegatedCard(Card delegatedCard) {
		this.delegatedCard = delegatedCard;
	}

	public void clearDelegateCard() {
		this.delegatedCard = null;
	}

	public void clearActiveCard(ActorRef out) {
		int pos = userPlayer.getHandCardPosition(activeCard);
		if (pos == -1) {
			throw new IllegalStateException("can't find active card position: " + activeCard);
		}
		BasicCommands.drawCard(out, activeCard, pos + 1, Card.CARD_NORMAL_MODE);
		this.activeCard = null;
	}

	public void clearActiveUnit() {
		this.activeUnit = null;
	}

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

	public void deductManaFromPlayer(ActorRef out, int manaCost, int mode) {
		Player player;
		if (mode == USER_MODE) {
			player = userPlayer;
		} else if (mode == AI_MODE) {
			player = aiPlayer;
		} else {
			throw new IllegalArgumentException("Invalid mode: " + mode);
		}
		if (manaCost > player.getMana()) {
			throw new IllegalStateException("More mana cost than what player has");
		}
		int manaBefore = player.getMana();
		int manaAfter = manaBefore - manaCost;
		player.setMana(manaAfter);
		if (player == userPlayer) {
			BasicCommands.setPlayer1Mana(out, player);
		} else {
			BasicCommands.setPlayer2Mana(out, player);
		}
	}

	public void deleteUserCard(ActorRef out, Card card) {
		int pos = userPlayer.removeHandCard(card);
		BasicCommands.deleteCard(out, pos + 1);
	}

	public Tile getTileByPos(int tilex, int tiley) {
		if (isValidPosition(tilex, tiley)) {
			return gameTiles[tilex][tiley];
		}
		return null;
	}

	public List<Tile> getAdjacentTiles(Tile tile) {
		List<Tile> result = new ArrayList<>();
		int startX = tile.getTilex() - 1;
		int startY = tile.getTiley() - 1;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Tile nearyBy = getTileByPos(startX + i, startY + j);
				if (nearyBy != null) {
					result.add(nearyBy);
				}
			}
		}
		return result;
	}

	public List<Tile> getAdjacentUnoccupiedTiles(Tile tile) {
		List<Tile> result = new ArrayList<>();
		List<Tile> adjacentTiles = getAdjacentTiles(tile);
		for (Tile cur : adjacentTiles) {
			if (!cur.isOccupied()) {
				result.add(cur);
			}
		}
		return result;
	}

	public Tile getUnitTile(Unit unit) {
		Position pos = unit.getPosition();
		int posX = pos.getTilex();
		int posY = pos.getTiley();
		return getTileByPos(posX, posY);
	}

	public List<Tile> getTilesUnitCanMoveTo(Unit unit) {
		List<Tile> accessibleTiles = new ArrayList<>();
		Tile currentTile = getUnitTile(unit);
		int currentX = currentTile.getTilex();
		int currentY = currentTile.getTiley();
		for (Tile targetTile : getGameTiles()) {
			if (targetTile.isOccupied()) {
				continue;
			}
			int tileX = targetTile.getTilex();
			int tileY = targetTile.getTiley();
			int diffX = Math.abs(tileX - currentX);
			int diffY = Math.abs(tileY - currentY);
			// Determine if the target Tile is within moving range
			boolean inRange = (diffX <= 2 && tileY == currentY) || (diffY <= 2 && tileX == currentX)
					|| (diffX == 1 && diffY == 1);
			if (!inRange) {
				continue;
			}
			if (!isPathBlocked(currentTile, targetTile)) {
				accessibleTiles.add(targetTile);
			}
		}
		return accessibleTiles;
	}

	// Check that the path from start to end is not blocked
	private boolean isPathBlocked(Tile departure, Tile destination) {
		// Calculate the difference between the starting point and the end point
		int deltaX = destination.getTilex() - departure.getTilex();
		int deltaY = destination.getTiley() - departure.getTiley();
		// Determining the direction of movement
		int stepX = Integer.signum(deltaX);
		int stepY = Integer.signum(deltaY);
		// Handling of cardinal paths
		if (deltaX == 0 || deltaY == 0) {
			return checkCardinalPathBlocked(departure, destination, stepX, stepY);
		} else { // For diagonal paths
			return checkDiagonalPathBlocked(departure, destination, stepX, stepY);
		}
	}

	private boolean checkCardinalPathBlocked(Tile departure, Tile destination, int stepX, int stepY) {
		int currentX = departure.getTilex();
		int currentY = departure.getTiley();
		while (currentX != destination.getTilex() || currentY != destination.getTiley()) {
			currentX += stepX;
			currentY += stepY;
			// Check if the current Tile is blocked
			Tile tile = getTileByPos(currentX, currentY);
			if (tile.isOccupied()) {
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
			if (tile.isOccupied()) {
				return true;
			}
			tile = getTileByPos(currentX, currentY + stepY);
			if (tile.isOccupied()) {
				return true;
			}
			currentX += stepX;
			currentY += stepY;
		}
		return false;
	}

	public List<Tile> getTilesWithEnemyUnitsInRange(Unit unit, int mode) {
		Tile unitOnTile = getUnitTile(unit);
		Set<Tile> tileSet = new HashSet<>();
		List<Tile> accessibleTiles = getTilesUnitCanMoveTo(unit);
		accessibleTiles.add(unitOnTile);
		for (Tile t : accessibleTiles) {
			List<Tile> tilesNearby = getAdjacentTiles(t);
			for (Tile nearby : tilesNearby) {
				if (nearby.isOccupied()) {
					if (mode == USER_MODE && isAiUnit(nearby.getUnit())) {
						tileSet.add(nearby);
					} else if (mode == AI_MODE && isUserUnit(nearby.getUnit())) {
						tileSet.add(nearby);
					}
				}
			}
		}
		return new ArrayList<>(tileSet);
	}

	public List<Tile> getTilesForSummon(int mode) {
		List<Tile> unitTiles;
		if (mode == USER_MODE) {
			unitTiles = getUserUnitTiles();
		} else if (mode == AI_MODE) {
			unitTiles = getAiUnitTiles();
		} else {
			throw new IllegalArgumentException("Invlaid mode: " + mode);
		}
		Set<Tile> tileSet = new HashSet<>();
		for (Tile tile : unitTiles) {
			List<Tile> freeAdjacentTiles = getAdjacentUnoccupiedTiles(tile);
			tileSet.addAll(freeAdjacentTiles);
		}
		return new ArrayList<>(tileSet);
	}

	public List<Unit> getUserUnits() {
		return userPlayer.getAllUnits();
	}

	public List<Tile> getUserUnitTiles() {
		return userPlayer.getAllTiles();
	}

	public List<Unit> getAiUnits() {
		return aiPlayer.getAllUnits();
	}

	public List<Tile> getAiUnitTiles() {
		return aiPlayer.getAllTiles();
	}

	public List<Unit> getAllUnits() {
		List<Unit> result = new ArrayList<>();
		result.addAll(getUserUnits());
		result.addAll(getAiUnits());
		return result;
	}

	public boolean isUserUnit(Unit unit) {
		return userPlayer.hasUnit(unit);
	}

	public boolean isAiUnit(Unit unit) {
		return aiPlayer.hasUnit(unit);
	}

	public void updateProvokeAreas() {
		userProvokeAreas.clear();
		aiProvokeAreas.clear();
		for (Unit unit : userPlayer.getAllUnits()) {
			if (unit.hasProvokeAbility()) {
				Tile tile = getUnitTile(unit);
				List<Tile> adjacentTiles = getAdjacentTiles(tile);
				userProvokeAreas.addAll(adjacentTiles);
			}
		}
		for (Unit unit : aiPlayer.getAllUnits()) {
			if (unit.hasProvokeAbility()) {
				Tile tile = getUnitTile(unit);
				List<Tile> adjacentTiles = getAdjacentTiles(tile);
				aiProvokeAreas.addAll(adjacentTiles);
			}
		}
	}

	public void adjustUnitPosition(Unit unit, Tile destination) {
		Player player = null;
		if (isUserUnit(unit)) {
			player = userPlayer;
		} else if (isAiUnit(unit)) {
			player = aiPlayer;
		} else {
			throw new IllegalStateException("Orphan unit: " + unit);
		}
		player.removeUnit(unit);
		player.putUnitOnTile(destination, unit);
		if (unit.hasProvokeAbility()) {
			updateProvokeAreas();
		}
	}

	public boolean isUnitProvoked(Unit unit) {
		Tile tile = getUnitTile(unit);
		if (isUserUnit(unit)) {
			return aiProvokeAreas.contains(tile);
		}
		if (isAiUnit(unit)) {
			return userProvokeAreas.contains(tile);
		}
		throw new IllegalStateException("Orphan unit:" + unit);
	}

	public boolean canUnitMove(Unit unit) {
		if (unit.isNewlySpawned()) {
			return false;
		}
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

	public String reasonUnitCannotMove(Unit unit) {
		if (unit.isNewlySpawned()) {
			return "unit is just spawned";
		}
		if (unit.getHasMoved()) {
			return "unit has already moved in this turn";
		}
		if (unit.getHasAttacked()) {
			return "unit has attacked and forbiden to move";
		}
		if (unit.isStunned()) {
			return "unit is stunned";
		}
		if (isUnitProvoked(unit)) {
			return "unit is provoked";
		}
		throw new IllegalStateException("no reason why unit cannot move");
	}

	public boolean canPerformAttack(Unit attacker, Unit attacked) {
		if (attacker.isNewlySpawned()) {
			return false;
		}
		if (attacker.getHasAttacked()) {
			return false;
		}
		if (attacker.isStunned()) {
			return false;
		}
		if (attacked != null && isUnitProvoked(attacker) && !attacked.hasProvokeAbility()) {
			return false;
		}
		return true;
	}

	public String reasonCannotPerformAttack(Unit attacker, Unit attacked) {
		if (attacker.isNewlySpawned()) {
			return "unit is just spawned";
		}
		if (attacker.getHasAttacked()) {
			return "attacker has already attacked in this turn";
		}
		if (attacker.isStunned()) {
			return "attacker is stunned";
		}
		if (attacked != null && isUnitProvoked(attacker) && !attacked.hasProvokeAbility()) {
			return "attacker is under provoked effect, can only atttack enemy with provoke ability";
		}
		throw new IllegalStateException("no reason why attack cannot be performed!");
	}

	public void redrawAllTiles(ActorRef out) {
		List<Tile> tiles = getGameTiles();
		for (Tile tile : tiles) {
			BasicCommands.drawTile(out, tile, Tile.TILE_NORMAL_MODE);
		}
	}

	public List<Tile> getTilesCreaturesToPlace(ActorRef out, Tile tile, int tileStyle) {
		List<Tile> result = new ArrayList<>();
		int x = tile.getTilex() - 1;
		int y = tile.getTiley() - 1;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int posX = x + i;
				int posY = y + i;
				if (isValidPosition(posX, posY)) {
					Tile targetTile = getTileByPos(posX, posY);
					if (targetTile.getUnit() == null) {
						result.add(targetTile);
					}
				}
			}
		}
		return result;
	}

	public void summonUnit(ActorRef out, Tile tile, Class<? extends Unit> unitClass, String configPath, int mode,
			boolean isWraithling) {
		if (tile.isOccupied()) {
			throw new IllegalStateException("cannot summon unit on the occupied tile: " + tile);
		}
		Unit unit = BasicObjectBuilders.loadUnit(configPath, genUnitId(), unitClass);
		if (unitClass != SaberspineTiger.class) {
			unit.setNewlySpawned(true);
		}
		if (mode == USER_MODE) {
			userPlayer.putUnitOnTile(tile, unit);
		} else if (mode == AI_MODE) {
			aiPlayer.putUnitOnTile(tile, unit);
		} else {
			throw new IllegalArgumentException("Invalid mode: " + mode);
		}
		String summonEffectPath = StaticConfFiles.f1_summon;
		if (isWraithling) {
			summonEffectPath = StaticConfFiles.f1_wraithsummon;
		}
		playEffectAnimation(out, summonEffectPath, tile);
		BasicCommands.drawUnit(out, unit, tile);
		BasicCommands.setUnitHealth(out, unit, unit.getHealth());
		BasicCommands.setUnitAttack(out, unit, unit.getAttack());
		if (unit.hasProvokeAbility()) {
			updateProvokeAreas();
		}
		triggerGambitAbilities(out, mode);
	}

	public void summonWraithlingOnRandomlySelectedUnoccupiedAdjacentTile(ActorRef out, Tile tile, int mode) {
		List<Tile> freeTiles = getAdjacentUnoccupiedTiles(tile);
		if (!freeTiles.isEmpty()) {
			int n = GameState.nextRandInt(freeTiles.size());
			Tile targetTile = freeTiles.get(n);
			summonWraithling(out, targetTile, mode);
		}
	}

	public void summonWraithling(ActorRef out, Tile tile, int mode) {
		summonUnit(out, tile, Wraithling.class, StaticConfFiles.wraithling, mode, true);
	}

	public void removeUnit(ActorRef out, Unit unit) {
		if (isUserUnit(unit)) {
			userPlayer.removeUnit(unit);
		} else {
			aiPlayer.removeUnit(unit);
		}
		BasicCommands.deleteUnit(out, unit);
	}

	public void handleUnitDeath(ActorRef out, Unit unitDead) {
		playUnitAnimation(out, unitDead, UnitAnimationType.death);
		BasicCommands.deleteUnit(out, unitDead);
		if (isUserUnit(unitDead)) {
			userPlayer.removeUnit(unitDead);
		} else {
			aiPlayer.removeUnit(unitDead);
		}
		if (unitDead == userAvatar || unitDead == aiAvatar) {
			gameOver = true;
			hasMovingUnit = false;
			gameMode = END_GAME_MODE;
			return;
		}
		if (unitDead.hasProvokeAbility()) {
			updateProvokeAreas();
		}
		triggerDeathwatchAbilities(out);
	}

	public boolean dealDamangeToUnit(ActorRef out, Unit unit, int damage) {
		if (unit.getShieldBuff() > 0) {
			int n = unit.getShieldBuff();
			unit.setShieldBuff(n - 1);
			return false;
		}
		playUnitAnimation(out, unit, UnitAnimationType.hit);
		int healthBefore = unit.getHealth();
		int healthAfter = Math.max(0, healthBefore - damage);
		unit.setHealth(healthAfter);
		BasicCommands.setUnitHealth(out, unit, healthAfter);
		if (unit == userAvatar) {
			userPlayer.setHealth(healthAfter);
			BasicCommands.setPlayer1Health(out, userPlayer);
		} else if (unit == aiAvatar) {
			aiPlayer.setHealth(healthAfter);
			BasicCommands.setPlayer2Health(out, aiPlayer);
			triggerZealAbility(out);
		}
		if (healthAfter == 0) {
			handleUnitDeath(out, unit);
		}
		return true;
	}

	public void healUnit(ActorRef out, Unit unit, int healPoint) {
		int healthBefore = unit.getHealth();
		int healthAfter = Math.min(unit.getMaxHealth(), healthBefore + healPoint);
		unit.setHealth(healthAfter);
		BasicCommands.setUnitHealth(out, unit, healthAfter);
		if (unit == aiAvatar) {
			aiPlayer.setHealth(healthAfter);
			BasicCommands.setPlayer2Health(out, aiPlayer);
		} else if (unit == userAvatar) {
			userPlayer.setHealth(healthAfter);
			BasicCommands.setPlayer1Health(out, userPlayer);
		}
	}

	public void triggerDeathwatchAbilities(ActorRef out) {
		for (Unit unit : getAllUnits()) {
			unit.performAbility(AbilityType.DEATH_WATCH, out, this);
		}
	}

	public void triggerGambitAbilities(ActorRef out, int mode) {
		List<Unit> units = null;
		if (mode == USER_MODE) {
			units = getUserUnits();
		} else if (mode == AI_MODE) {
			units = getAiUnits();
		} else {
			throw new IllegalArgumentException("Invalid game mode: " + mode);
		}
		for (Unit unit : units) {
			unit.performAbility(AbilityType.OPENING_GAMBIT, out, this);
		}
	}

	public void triggerZealAbility(ActorRef out) {
		for (Unit u : getAiUnits()) {
			u.performAbility(AbilityType.ZEAL, out, this);
		}
	}

	// --------------------------------------------------------------------------------------------------------
	public static int nextRandInt(int bound) {
		return rand.nextInt(bound);
	}

	public static int genUnitId() {
		return unitIdCounter++;
	}

	public static int genCardId() {
		return cardIdCounter++;
	}

	public static void playUnitAnimation(ActorRef out, Unit unit, UnitAnimationType animationType) {
		int n = BasicCommands.playUnitAnimation(out, unit, animationType);
		BasicCommands.sleep(n);
	}

	public static void playEffectAnimation(ActorRef out, String effectConfigPath, Tile tile) {
		EffectAnimation effect = BasicObjectBuilders.loadEffect(effectConfigPath);
		int n = BasicCommands.playEffectAnimation(out, effect, tile);
		BasicCommands.sleep(n);
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
}
