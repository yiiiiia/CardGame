package structures.basic.unit;

import java.util.Collections;
import java.util.List;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Unit;

public class SkyrockGolem extends Unit {

	public SkyrockGolem() {
		name = "SkyrockGolem";
		health = 2;
		maxHealth = 2;
		attack = 4;
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
