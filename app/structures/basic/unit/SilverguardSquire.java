package structures.basic.unit;

import java.util.List;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.AbilityType;
import structures.basic.Tile;
import structures.basic.Unit;

public class SilverguardSquire extends Unit {

	public SilverguardSquire() {
		name = "SilverguardSquire";
		health = 1;
		maxHealth = 1;
		attack = 1;
	}

	public void performGambit(ActorRef out, GameState gameState) {
		Unit aiAvatar = gameState.getAiAvatar();
		Tile tile = gameState.getUnitTile(aiAvatar);
		int posX = tile.getTilex();
		int posY = tile.getTiley();
		Tile left = gameState.getTileByPos(posX - 1, posY);
		if (left != null && left.isOccupied()) {
			Unit leftUnit = left.getUnit();
			if (gameState.isAiUnit(leftUnit)) {
				leftUnit.incrAttack();
				leftUnit.incrHealth();
				BasicCommands.setUnitAttack(out, leftUnit, leftUnit.getAttack());
				BasicCommands.setUnitHealth(out, leftUnit, leftUnit.getHealth());
			}
		}
		Tile right = gameState.getTileByPos(posX + 1, posY);
		if (right != null && right.isOccupied()) {
			Unit rightUnit = right.getUnit();
			if (gameState.isAiUnit(rightUnit)) {
				rightUnit.incrAttack();
				rightUnit.incrHealth();
				BasicCommands.setUnitAttack(out, rightUnit, rightUnit.getAttack());
				BasicCommands.setUnitHealth(out, rightUnit, rightUnit.getHealth());
			}
		}
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
		return List.of(AbilityType.PROVOKE);
	}
}
