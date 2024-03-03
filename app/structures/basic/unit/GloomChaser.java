package structures.basic.unit;

import java.util.List;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;

public class GloomChaser extends Unit {
	public GloomChaser() {
		name = "GloomChaser";
		health = 1;
		maxHealth = 1;
		attack = 3;
	}

	public void performGambit(ActorRef out, GameState gameState) {
		Position position = this.getPosition();
		int posX = position.getTilex();
		int posY = position.getTiley();
		Tile tile = gameState.getTileByPos(posX - 1, posY);
		if (tile == null || tile.isOccupied()) {
			return;
		}
		gameState.summonWraithling(out, tile, GameState.USER_MODE);
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
