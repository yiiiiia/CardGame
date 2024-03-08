package structures.basic.unit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Tile;
import structures.basic.Unit;

public class YoungFlamewing extends Unit {

	public YoungFlamewing() {
		name = "YoungFlamewing";
		health = 4;
		maxHealth = 4;
		attack = 5;
	}

	@Override
	public void performAbility(AbilityType type, ActorRef out, GameState gameState) {
		if (type == AbilityType.FLYING) {
			performFlying(out, gameState);
		}
		return;
	}

	@Override
	public List<AbilityType> getAbilityTypes() {
		return List.of(AbilityType.FLYING);
	}

	public Set<Tile> getTilesUnitCanMoveTo(GameState gameState) {
		Set<Tile> tiles = new HashSet<>();
		for (Tile t : gameState.getGameTiles()) {
			if (!t.isOccupied()) {
				tiles.add(t);
			}
		}
		return tiles;
	}

	private void performFlying(ActorRef out, GameState gameState) {
		Unit userAvatar = gameState.getUserAvatar();
		if (GameState.unitsAdjacent(this, userAvatar)) {
			// already adjacent to user avatar, check to see if it has blocked other units
			Tile tileAdjustTo = gameState.needAdjustPosition(gameState.getUnitTile(userAvatar),
					gameState.getUnitTile(this));
			if (tileAdjustTo != null) {
				move(out, gameState, tileAdjustTo);
			}
			return;
		}
		Tile userAvatarTile = gameState.getUnitTile(userAvatar);
		Set<Tile> tiles = gameState.getAdjacentUnoccupiedTiles(userAvatarTile);
		while (!tiles.isEmpty()) {
			Tile target = gameState.findTileClosestToUnit(this, tiles);
			if (gameState.getUserProvokeAreas().contains(target)) {
				tiles.remove(target);
			} else {
				move(out, gameState, target);
				break;
			}
		}
	}
}
