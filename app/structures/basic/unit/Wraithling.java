package structures.basic.unit;

import java.util.Collections;
import java.util.List;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Unit;

public class Wraithling extends Unit {

	public Wraithling() {
		name = "Wraithling";
		health = 1;
		maxHealth = 1;
		attack = 1;
	}

	@Override
	public void performAbility(AbilityType type, ActorRef out, GameState gameState) {
		// do nothing
	}

	@Override
	public List<AbilityType> getAbilityTypes() {
		return Collections.emptyList();
	}
}
