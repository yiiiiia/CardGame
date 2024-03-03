package structures.basic.unit;

import java.util.List;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Unit;

public class SwampEntangler extends Unit {

	public SwampEntangler() {
		name = "SwampEntangler";
		health = 3;
		maxHealth = 3;
		attack = 0;
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
