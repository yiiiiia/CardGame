package structures.basic.unit;

import java.util.List;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;

public class NightsorrowAssassin extends Unit {

	public NightsorrowAssassin() {
		name = "NightsorrowAssassin";
		health = 2;
		maxHealth = 2;
		attack = 4;
	}

	public void performGambit(ActorRef out, GameState gameState) {
		for (Unit enemy : gameState.getAllAIUnits()) {
			if (GameState.unitsAdjacent(this, enemy) && enemy.getHealth() < enemy.getMaxHealth()
					&& enemy != gameState.getUserAvatar()) {
				GameState.playUnitAnimation(out, enemy, UnitAnimationType.death);
				gameState.removeUnit(out, enemy);
			}
		}
	}

	public NightsorrowAssassin getInstance(String configpaths) {
		return (NightsorrowAssassin) BasicObjectBuilders.loadUnit(configpaths, 6, NightsorrowAssassin.class);
	}

	@Override
	public void performAbility(AbilityType type, ActorRef out, GameState gameState) {
		if (type != AbilityType.OPENING_GAMBIT) {
			return;
		}
		performGambit(out, gameState);
	}

	@Override
	public List<AbilityType> getAbilityTypes() {
		return List.of(AbilityType.OPENING_GAMBIT);
	}
}
