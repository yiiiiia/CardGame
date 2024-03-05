package structures.basic.unit;

import java.util.List;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Unit;

public class SaberspineTiger extends Unit {

	public SaberspineTiger() {
		name = "SaberspineTiger";
		health = 2;
		maxHealth = 2;
		attack = 3;
	}

	@Override
	public void performAbility(AbilityType type, ActorRef out, GameState gameState) {
		if (type != AbilityType.RUSH) {
			return;
		}
		performRush(out, gameState);
	}

	@Override
	public List<AbilityType> getAbilityTypes() {
		return List.of(AbilityType.RUSH);
	}

	private void performRush(ActorRef out, GameState gameState) {
		// TODO integrated with AI logic
	}
}