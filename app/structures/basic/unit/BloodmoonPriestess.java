package structures.basic.unit;

import java.util.List;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class BloodmoonPriestess extends Unit {
	public BloodmoonPriestess() {
		name = "BloodmoonPriestess";
		health = 3;
		maxHealth = 3;
		attack = 3;
	}

	public void performDeathWatch(ActorRef out, GameState gameState) {
		Tile tile = gameState.getUnitTile(this);
		gameState.summonWraithlingOnRandomlySelectedUnoccupiedAdjacentTile(out, tile, GameState.USER_MODE);
	}

	public static BloodmoonPriestess getInstance(String configpaths) {
		return (BloodmoonPriestess) BasicObjectBuilders.loadUnit(configpaths, 7, BloodmoonPriestess.class);
	}

	@Override
	public void performAbility(AbilityType type, ActorRef out, GameState gameState) {
		if (type != AbilityType.DEATH_WATCH) {
			return;
		}
		performDeathWatch(out, gameState);
	}

	@Override
	public List<AbilityType> getAbilityTypes() {
		return List.of(AbilityType.DEATH_WATCH);
	}
}