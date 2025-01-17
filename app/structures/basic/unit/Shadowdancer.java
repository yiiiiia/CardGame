package structures.basic.unit;

import java.util.List;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Tile;
import structures.basic.Unit;
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

		Tile aiTile = gameState.getUnitTile(aiAvatar);
		GameState.playEffectAnimation(out, StaticConfFiles.f1_inmolation, aiTile);
		gameState.dealDamangeToUnit(out, aiAvatar, 1);

		Tile userTile = gameState.getUnitTile(userAvatar);
		GameState.playEffectAnimation(out, StaticConfFiles.f1_buff, userTile);
		gameState.healUnit(out, userAvatar, 1);
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
