package structures.basic.unit;

import java.util.List;

import structures.GameState;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class IroncliffeGuardian extends Unit {
    
    public static final int initialHealth = 10;
    public static final int initialAttack = 3;

    protected int health;
    protected int attack;

    public IroncliffeGuardian() {
        super();
        health = 10;
        attack = 3;
    }

    public void performProvoke(ActorRef out, GameState gameState) {
        List<Unit> unitsToProvoke = gameState.unitsWithinAttackRange(this);
        for(Unit unit: unitsToProvoke) {
            unit.setProvoked(true);
            //gameState.isUnitProvoked(this) = true;
            //not sure if i should access it from unit class or gamestate class
        }
    }

    public IroncliffeGuardian getInstance(String configpaths) {
        return (IroncliffeGuardian)BasicObjectBuilders.loadUnit(configpaths, 16, IroncliffeGuardian.class);
    }
}
