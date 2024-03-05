package structures.basic.unit;

import java.util.List;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class Shadowdancer extends Unit {

	public Shadowdancer() {
		name = "Shadowdancer";
		health = 4;
		maxHealth = 4;
		attack = 5;
	}

	private void performDeathWatch(ActorRef out, GameState gameState) {
		Unit aiAvatar = gameState.getAiAvatar();
		Unit userAvatar = gameState.getUserAvatar();
		gameState.dealDamangeToUnit(out, aiAvatar, 1);
		Tile tile = gameState.getUnitTile(userAvatar);
		GameState.playEffectAnimation(out, StaticConfFiles.f1_buff, tile);
		gameState.healUnit(out, userAvatar, 1);
	}

	public static Shadowdancer getInstance(String configpaths) {
		return (Shadowdancer) BasicObjectBuilders.loadUnit(configpaths, 8, Shadowdancer.class);
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
