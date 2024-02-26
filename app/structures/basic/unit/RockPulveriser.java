package structures.basic.unit;

import java.util.List;

import structures.GameState;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class RockPulveriser extends Unit {

    public static final int initialHealth = 4;
    public static final int initialAttack = 1;

    protected int health;
    protected int attack;
    protected boolean hasProvoke;
    
    public RockPulveriser() {
        super();
        health = 4;
        attack = 1;
        hasProvoke = true;
    }

    public void performProvoke (ActorRef out, GameState gameState) {
        List<Unit> unitsToProvoke = gameState.unitsWithinAttackRange(this);
        for(Unit unit: unitsToProvoke) {
            unit.setProvoked(true);
            //gameState.isUnitProvoked(this) = true;
            //not sure if i should access it from unit class or gamestate class
        }
    }

    public RockPulveriser getInstance(String configpaths) {
        return (RockPulveriser)BasicObjectBuilders.loadUnit(configpaths, 4, RockPulveriser.class);
    }
}
