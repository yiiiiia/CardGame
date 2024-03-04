package structures.basic.unit;

import java.util.Collections;
import java.util.List;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Unit;

public class AIAvatar extends Unit {

	public AIAvatar() {
		name = "AI Avatar";
		health = 20;
		maxHealth = 20;
		attack = 2;
	}

	@Override
	public void performAbility(AbilityType type, ActorRef out, GameState gameState) {
		return;
	}

	@Override
	public List<AbilityType> getAbilityTypes() {
		return Collections.emptyList();
	}
}
