package structures.basic.unit;

import java.util.List;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Unit;

public class YoungFlamewing extends Unit {

	public YoungFlamewing() {
		name = "YoungFlamewing";
		health = 4;
		maxHealth = 4;
		attack = 5;
	}

	@Override
	public void performAbility(AbilityType type, ActorRef out, GameState gameState) {
		if (type == AbilityType.FLYING) {
			performFlying(out, gameState);
		}
		return;
	}

	@Override
	public List<AbilityType> getAbilityTypes() {
		return List.of(AbilityType.FLYING);
	}

	private void performFlying(ActorRef out, GameState gameState) {
		// TODO integrate with AI logic
	}
}
