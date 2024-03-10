package structures.basic.unit;

import java.util.List;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.StaticConfFiles;

public class SilverguardKnight extends Unit {

	public SilverguardKnight() {
		name = "SilverguardKnight";
		health = 5;
		maxHealth = 5;
		attack = 1;
	}

	public void performProvoke(ActorRef out, GameState gameState) {
		gameState.updateProvokeAreas();
	}

	public void performZeal(ActorRef out, GameState gamestate) {
		Tile tile = gamestate.getUnitTile(this);
		GameState.playEffectAnimation(out, StaticConfFiles.f1_buff, tile);
		setAttack(getAttack() + 2);
		BasicCommands.setUnitAttack(out, this, getAttack());
		BasicCommands.sleep(500);
	}

	@Override
	public void performAbility(AbilityType type, ActorRef out, GameState gameState) {
		if (type == AbilityType.PROVOKE) {
			performProvoke(out, gameState);
			return;
		}
		if (type == AbilityType.ZEAL) {
			performZeal(out, gameState);
			return;
		}
		return;
	}

	@Override
	public List<AbilityType> getAbilityTypes() {
		return List.of(AbilityType.PROVOKE, AbilityType.ZEAL);
	}
}
