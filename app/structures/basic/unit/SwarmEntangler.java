package structures.basic.unit;

import java.util.List;

import structures.GameState;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class SwarmEntangler extends Unit{

    public static final int initialHealth = 3;
    public static final int initialAttack = 0;

    protected int health;
    protected int attack;
    protected boolean hasProvoke;

    public SwarmEntangler() {
        super();
        health = 3;
        attack = 0;
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

    public SwarmEntangler getInstance(String configpaths) {
        return (SwarmEntangler)BasicObjectBuilders.loadUnit(configpaths, 10, SwarmEntangler.class);
    }
}
