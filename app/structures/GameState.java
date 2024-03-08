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
import structures.basic.unit.YoungFlamewing;
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
	private int turn = 0; // Number of turns

	// Collections to track game elements
	private Tile[][] gameTiles;
	private Player userPlayer;
	private Player aiPlayer;
	private Action pendingAction; // Action to perform after a unit stops
	private Unit activeUnit; // Currently selected unit
	private Card activeCard;
	private Unit userAvatar;
	private Unit aiAvatar;
	private Card delegatedCard; // delegated card to hand click event
	// areas that ai units will be provoked
	private Set<Tile> userProvokeAreas;
	// areas that user units will be provoked
	private Set<Tile> aiProvokeAreas;
	// tiles that are highlighted in the game currently
	Set<Tile> highlightedTiles;

	public GameState() {
		userProvokeAreas = new HashSet<>();
		aiProvokeAreas = new HashSet<>();
		highlightedTiles = new HashSet<>();
	}

	public boolean isGameInitialised() {
		return gameInitialised;
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

	public boolean isInAIMode() {
		return gameMode == AI_MODE;
	}

	public void setGameToAIMode() {
		gameMode = AI_MODE;
	}

	public void setGameToUserMode() {
		gameMode = USER_MODE;
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

	public void addTurn() {
		turn++;
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

	public void drawAndRecordHighlightedTile(ActorRef out, Tile tile, int tileMode) {
		if (tileMode == Tile.TILE_NORMAL_MODE) {
			throw new IllegalStateException("this method cannot be used to draw normal tile");
		}
		BasicCommands.drawTile(out, tile, tileMode);
		highlightedTiles.add(tile);
	}

	public void clearHighlightedTiles(ActorRef out) {
		for (Tile tile : highlightedTiles) {
			BasicCommands.drawTile(out, tile, Tile.TILE_NORMAL_MODE);
		}
		highlightedTiles.clear();
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
				Tile tileNearby = getTileByPos(startX + i, startY + j);
				if (tileNearby != null) {
					result.add(tileNearby);
				}
			}
		}
		return result;
	}

	public Set<Tile> getAdjacentUnoccupiedTiles(Tile tile) {
		Set<Tile> result = new HashSet<>();
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

	public Set<Tile> getTilesUnitCanMoveTo(Unit unit) {
		if (unit.getClass() == YoungFlamewing.class) {
			YoungFlamewing yf = (YoungFlamewing) unit;
			return yf.getTilesUnitCanMoveTo(this);
		}

		Set<Tile> result = new HashSet<>();
		Tile curTile = getUnitTile(unit);
		int curX = curTile.getTilex();
		int curY = curTile.getTiley();
		for (Tile targetTile : getGameTiles()) {
			if (targetTile.isOccupied()) {
				continue;
			}
			int tileX = targetTile.getTilex();
			int tileY = targetTile.getTiley();
			int diffX = Math.abs(tileX - curX);
			int diffY = Math.abs(tileY - curY);
			// Determine if the target Tile is within moving range
			boolean inRange = (diffX <= 2 && tileY == curY) || (diffY <= 2 && tileX == curX)
					|| (diffX == 1 && diffY == 1);
			if (!inRange) {
				continue;
			}
			if (!isPathBlocked(curTile, targetTile)) {
				result.add(targetTile);
			}
		}
		return result;
	}

	// Check that there's a path from start to end
	private boolean isPathBlocked(Tile startTile, Tile endTile) {
		int startX = startTile.getTilex();
		int startY = startTile.getTiley();
		int endX = endTile.getTilex();
		int endY = endTile.getTiley();
		if (startX == endX) {
			int direction = Integer.signum(endY - startY);
			for (int y = startY + direction; y != endY; y += direction) {
				if (getTileByPos(startX, y).isOccupied()) {
					return true;
				}
			}
			return false;
		}
		if (startY == endY) {
			int direction = Integer.signum(endX - startX);
			for (int x = startX + direction; x != endX; x += direction) {
				if (getTileByPos(x, startY).isOccupied()) {
					return true;
				}
			}
			return false;
		}
		if (endY > startY && endX > startX) {
			Tile t1 = getTileByPos(startX + 1, startY);
			Tile t2 = getTileByPos(startX, startY + 1);
			return t1.isOccupied() && t2.isOccupied();
		}
		if (endY < startY && endX > startX) {
			Tile t1 = getTileByPos(startX + 1, startY);
			Tile t2 = getTileByPos(startX, startY - 1);
			return t1.isOccupied() && t2.isOccupied();
		}
		if (endY > startY && endX < startX) {
			Tile t1 = getTileByPos(startX - 1, startY);
			Tile t2 = getTileByPos(startX, startY + 1);
			return t1.isOccupied() && t2.isOccupied();
		}
		if (endY < startY && endX < startX) {
			Tile t1 = getTileByPos(startX - 1, startY);
			Tile t2 = getTileByPos(startX, startY - 1);
			return t1.isOccupied() && t2.isOccupied();
		}
		return false;
	}

	public List<Tile> getTilesWithEnemyUnitsInRange(Unit unit, int mode) {
		Tile unitOnTile = getUnitTile(unit);
		Set<Tile> tileSet = new HashSet<>();
		Set<Tile> accessibleTiles = getTilesUnitCanMoveTo(unit);
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

	public Set<Tile> getTilesForSummon(int mode) {
		Set<Tile> tiles;
		if (mode == USER_MODE) {
			tiles = getAllUserTiles();
		} else if (mode == AI_MODE) {
			tiles = getAllAITiles();
		} else {
			throw new IllegalArgumentException("Invlaid mode: " + mode);
		}
		Set<Tile> result = new HashSet<>();
		for (Tile tile : tiles) {
			result.addAll(getAdjacentUnoccupiedTiles(tile));
		}
		return result;
	}

	public List<Unit> getAllUserUnits() {
		return userPlayer.getOwnUnits();
	}

	public Set<Tile> getAllUserTiles() {
		return new HashSet<Tile>(userPlayer.getAllTiles());
	}

	public List<Unit> getAllAIUnits() {
		return aiPlayer.getOwnUnits();
	}

	public Set<Tile> getAllAITiles() {
		return new HashSet<Tile>(aiPlayer.getAllTiles());
	}

	public List<Unit> getAllUnits() {
		List<Unit> result = new ArrayList<>();
		result.addAll(getAllUserUnits());
		result.addAll(getAllAIUnits());
		return result;
	}

	public boolean isUserUnit(Unit unit) {
		return userPlayer.hasUnit(unit);
	}

	public boolean isAiUnit(Unit unit) {
		return aiPlayer.hasUnit(unit);
	}

	public Set<Tile> getAiProvokeAreas() {
		return aiProvokeAreas;
	}

	public Set<Tile> getUserProvokeAreas() {
		return userProvokeAreas;
	}

	public void updateProvokeAreas() {
		userProvokeAreas.clear();
		aiProvokeAreas.clear();
		for (Unit unit : userPlayer.getOwnUnits()) {
			if (unit.hasProvokeAbility()) {
				Tile tile = getUnitTile(unit);
				List<Tile> adjacentTiles = getAdjacentTiles(tile);
				userProvokeAreas.addAll(adjacentTiles);
			}
		}
		for (Unit unit : aiPlayer.getOwnUnits()) {
			if (unit.hasProvokeAbility()) {
				Tile tile = getUnitTile(unit);
				List<Tile> adjacentTiles = getAdjacentTiles(tile);
				aiProvokeAreas.addAll(adjacentTiles);
			}
		}
	}

	public void updateUnitPosition(Unit unit, Tile destination) {
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

	public boolean unitIsProvoked(Unit unit) {
		Tile tile = getUnitTile(unit);
		if (isUserUnit(unit)) {
			return aiProvokeAreas.contains(tile);
		}
		if (isAiUnit(unit)) {
			return userProvokeAreas.contains(tile);
		}
		throw new IllegalStateException("Orphan unit:" + unit);
	}

	public boolean unitCanMove(Unit unit) {
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
		if (unitIsProvoked(unit)) {
			return false;
		}
		return true;
	}

	public String whyUnitCannotMove(Unit unit) {
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
		if (unitIsProvoked(unit)) {
			return "unit is provoked";
		}
		throw new IllegalStateException("no reason why unit cannot move");
	}

	public boolean unitCanAttack(Unit attacker, Unit attacked) {
		if (attacker.getAttack() == 0) {
			return false;
		}
		if (attacker.isNewlySpawned()) {
			return false;
		}
		if (attacker.getHasAttacked()) {
			return false;
		}
		if (attacker.isStunned()) {
			return false;
		}
		if (attacked != null && unitIsProvoked(attacker) && !attacked.hasProvokeAbility()) {
			return false;
		}
		return true;
	}

	public String whyUnitCannotAttack(Unit attacker, Unit attacked) {
		if (attacker.getAttack() == 0) {
			return "has 0 attack point";
		}
		if (attacker.isNewlySpawned()) {
			return "unit is just spawned";
		}
		if (attacker.getHasAttacked()) {
			return "attacker has already attacked in this turn";
		}
		if (attacker.isStunned()) {
			return "attacker is stunned";
		}
		if (attacked != null && unitIsProvoked(attacker) && !attacked.hasProvokeAbility()) {
			return "attacker is under provoked effect, can only atttack enemy with provoke ability";
		}
		throw new IllegalStateException("no reason why attack cannot be performed!");
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
		if (unitClass != SaberspineTiger.class) { // SaberspineTiger has Rush ability
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
		triggerGambitAbilities(out, mode, unit);
	}

	public void summonWraithling(ActorRef out, Tile tile, int mode) {
		summonUnit(out, tile, Wraithling.class, StaticConfFiles.wraithling, mode, true);
	}

	public void summonWraithlingOnRandomlySelectedUnoccupiedAdjacentTile(ActorRef out, Tile tile, int mode) {
		Set<Tile> freeTiles = getAdjacentUnoccupiedTiles(tile);
		List<Tile> tileList = new ArrayList<>(freeTiles);
		if (!freeTiles.isEmpty()) {
			int n = GameState.nextRandInt(freeTiles.size());
			Tile targetTile = tileList.get(n);
			summonWraithling(out, targetTile, mode);
		}
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
			BasicCommands.addPlayer1Notification(out, "User avatar robutness now: " + (n - 1), 3);
			BasicCommands.sleep(500);
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

	public void triggerGambitAbilities(ActorRef out, int mode, Unit toExclude) {
		List<Unit> units = null;
		if (mode == USER_MODE) {
			units = getAllUserUnits();
		} else if (mode == AI_MODE) {
			units = getAllAIUnits();
		} else {
			throw new IllegalArgumentException("Invalid game mode: " + mode);
		}
		for (Unit unit : units) {
			if (unit == toExclude) {
				continue;
			}
			unit.performAbility(AbilityType.OPENING_GAMBIT, out, this);
		}
	}

	public void triggerZealAbility(ActorRef out) {
		for (Unit u : getAllAIUnits()) {
			u.performAbility(AbilityType.ZEAL, out, this);
		}
	}

	public Tile findAttackPath(Unit attacker, Tile targetTile, int mode) {
		Set<Tile> tilesAccessible = getTilesUnitCanMoveTo(attacker);
		if (tilesAccessible.isEmpty()) {
			return null;
		}
		Set<Tile> candidates = new HashSet<>();
		for (Tile t : tilesAccessible) {
			if (tilesAdjacent(t, targetTile)) {
				if (mode == USER_MODE) {
					if (aiProvokeAreas.contains(t) && !targetTile.getUnit().hasProvokeAbility()) {
						continue;
					}
				} else {
					if (userProvokeAreas.contains(t) && !targetTile.getUnit().hasProvokeAbility()) {
						continue;
					}
				}
				candidates.add(t);
			}
		}
		return findTileClosestToUnit(attacker, candidates);
	}

	public Tile findTileClosestToUnit(Unit unit, Set<Tile> candidateTiles) {
		return findTileClosestToTile(getUnitTile(unit), candidateTiles);
	}

	public Tile findTileClosestToTile(Tile target, Set<Tile> candidateTiles) {
		Tile result = null;
		int minDistance = Integer.MAX_VALUE;
		for (Tile t : candidateTiles) {
			int distance = distanceBetweenTiles(target, t);
			if (distance < minDistance) {
				result = t;
				minDistance = distance;
			}
		}
		return result;
	}

	public void performAttackAndCounterAttack(ActorRef out, Unit attacker, Unit attacked) {
		if (!unitCanAttack(attacker, attacked)) {
			throw new IllegalStateException("cannot perform attack!");
		}
		attacker.doAttack(out, this, attacked, false);
		if (attacked.getHealth() > 0 && !attacked.isStunned()) {
			// perform counter attack, if the attacked is alive and not stunned
			attacked.doAttack(out, this, attacker, true);
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

	public static int distanceBetweenTiles(Tile t1, Tile t2) {
		return Math.abs(t1.getTilex() - t2.getTilex()) + Math.abs(t1.getTiley() - t2.getTiley());
	}

	public Tile needAdjustPosition(Tile userUnitTile, Tile aiUnitTile) {
		if (!isBlockingOtherAiUnits(userUnitTile, aiUnitTile)) {
			return null;
		}
		Set<Tile> tiles = getTilesUnitCanMoveTo(aiUnitTile.getUnit());
		if (userUnitTile.getTilex() == aiUnitTile.getTilex()) {
			Tile temp = getTileByPos(aiUnitTile.getTilex() + 1, userUnitTile.getTiley());
			if (temp != null && !temp.isOccupied() && !isBlockingOtherAiUnits(userUnitTile, temp)
					&& tiles.contains(temp)) {
				return temp;
			}
			temp = getTileByPos(aiUnitTile.getTilex() + 1, aiUnitTile.getTiley());
			if (temp != null && !temp.isOccupied() && !isBlockingOtherAiUnits(userUnitTile, temp)
					&& tiles.contains(temp)) {
				return temp;
			}
			temp = getTileByPos(aiUnitTile.getTilex() - 1, userUnitTile.getTiley());
			if (temp != null && !temp.isOccupied() && !isBlockingOtherAiUnits(userUnitTile, temp)
					&& tiles.contains(temp)) {
				return temp;
			}
			temp = getTileByPos(aiUnitTile.getTilex() - 1, aiUnitTile.getTiley());
			if (temp != null && !temp.isOccupied() && !isBlockingOtherAiUnits(userUnitTile, temp)
					&& tiles.contains(temp)) {
				return temp;
			}
		}
		if (userUnitTile.getTiley() == aiUnitTile.getTiley()) {
			Tile temp = getTileByPos(userUnitTile.getTilex(), aiUnitTile.getTiley() + 1);
			if (temp != null && !temp.isOccupied() && !isBlockingOtherAiUnits(userUnitTile, temp)
					&& tiles.contains(temp)) {
				return temp;
			}
			temp = getTileByPos(aiUnitTile.getTilex(), aiUnitTile.getTiley() + 1);
			if (temp != null && !temp.isOccupied() && !isBlockingOtherAiUnits(userUnitTile, temp)
					&& tiles.contains(temp)) {
				return temp;
			}
			temp = getTileByPos(userUnitTile.getTilex(), aiUnitTile.getTiley() - 1);
			if (temp != null && !temp.isOccupied() && !isBlockingOtherAiUnits(userUnitTile, temp)
					&& tiles.contains(temp)) {
				return temp;
			}
			temp = getTileByPos(aiUnitTile.getTilex(), aiUnitTile.getTiley() - 1);
			if (temp != null && !temp.isOccupied() && !isBlockingOtherAiUnits(userUnitTile, temp)
					&& tiles.contains(temp)) {
				return temp;
			}
		}
		return null;
	}

	private boolean isBlockingOtherAiUnits(Tile userUnitTile, Tile aiUnitTile) {
		if (!GameState.tilesAdjacent(userUnitTile, aiUnitTile)) {
			return false;
		}
		int ux = userUnitTile.getTilex();
		int uy = userUnitTile.getTiley();
		int ax = aiUnitTile.getTilex();
		int ay = aiUnitTile.getTiley();
		if (ux != ax && uy != ay) {
			return false;
		}
		if (ux == ax) {
			int direction = ay - uy;
			for (int y = ay + direction; y >= 0 && y < GameState.ROWS; y += direction) {
				Tile temp = getTileByPos(ax, y);
				if (temp != null && temp.isOccupied() && isAiUnit(temp.getUnit())) {
					return true;
				}
			}
			return false;
		}
		int direction = ax - ux;
		for (int x = ax + direction; x >= 0 && x < GameState.COLS; x += direction) {
			Tile temp = getTileByPos(x, ay);
			if (temp != null && temp.isOccupied() && isAiUnit(temp.getUnit())) {
				return true;
			}
		}
		return false;
	}
}
