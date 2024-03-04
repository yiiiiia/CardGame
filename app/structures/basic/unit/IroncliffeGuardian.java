package structures.basic.unit;

import java.util.List;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Unit;

public class IroncliffeGuardian extends Unit {

	public IroncliffeGuardian() {
		name = "IroncliffeGuardian";
		health = 10;
		maxHealth = 10;
		attack = 3;
	}

	@Override
	public void performAbility(AbilityType type, ActorRef out, GameState gameState) {
		if (type == AbilityType.PROVOKE) {
			performProvoke(out, gameState);
		}
		return;
	}

	@Override
	public List<AbilityType> getAbilityTypes() {
		return List.of(AbilityType.PROVOKE);
	}

	private void performProvoke(ActorRef out, GameState gameState) {
		gameState.updateProvokeAreas();
	}
}
