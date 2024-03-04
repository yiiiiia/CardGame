package structures.basic.unit;

import java.util.List;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Unit;

public class BadOmen extends Unit {

	public BadOmen() {
		name = "BadOmen";
		health = 1;
		maxHealth = 1;
		attack = 0;
	}

	public void performDeathWatch(ActorRef out, GameState gameState) {
		incrAttack();
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
