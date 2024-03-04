package structures.basic.unit;

import java.util.List;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Unit;

public class RockPulveriser extends Unit {

	public RockPulveriser() {
		name = "RockPulveriser";
		health = 4;
		maxHealth = 4;
		attack = 1;
	}

	public void performProvoke(ActorRef out, GameState gameState) {
		gameState.updateProvokeAreas();
	}

	@Override
	public void performAbility(AbilityType type, ActorRef out, GameState gameState) {
		if (type != AbilityType.PROVOKE) {
			return;
		}
		performProvoke(out, gameState);
	}

	@Override
	public List<AbilityType> getAbilityTypes() {
		return List.of(AbilityType.PROVOKE);
	}
}
