package structures.basic.unit;

import java.util.List;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.StaticConfFiles;

public class ShadowWatcher extends Unit {
	public ShadowWatcher() {
		name = "ShadowWatcher";
		health = 2;
		maxHealth = 2;
		attack = 3;
	}

	public void performDeathWatch(ActorRef out, GameState gameState) {
		Tile tile = gameState.getUnitTile(this);
		GameState.playEffectAnimation(out, StaticConfFiles.f1_buff, tile);
		// whenever a unit, friendly or enemy dies
		incrHealth();
		incrAttack();
		BasicCommands.setUnitHealth(out, this, health);
		BasicCommands.setUnitAttack(out, this, attack);
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
