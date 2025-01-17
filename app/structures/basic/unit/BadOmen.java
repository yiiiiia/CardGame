package structures.basic.unit;

import java.util.List;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.StaticConfFiles;

public class BadOmen extends Unit {

	public BadOmen() {
		name = "BadOmen";
		health = 1;
		maxHealth = 1;
		attack = 0;
	}

	public void performDeathWatch(ActorRef out, GameState gameState) {
		Tile tile = gameState.getUnitTile(this);
		GameState.playEffectAnimation(out, StaticConfFiles.f1_buff, tile);
		incrAttack();
		BasicCommands.setUnitAttack(out, this, attack);
	}

	@Override
	public void performAbility(AbilityType type, ActorRef out, GameState gameState) {
		if (type != AbilityType.DEATH_WATCH) {
			return;
		}
		performDeathWatch(out, gameState);
	}

	@Override
	public List<AbilityType> getAbilityTypes() {
		return List.of(AbilityType.DEATH_WATCH);
	}
}
