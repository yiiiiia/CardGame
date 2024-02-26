package structures.basic.unit;

import java.util.List;

import commands.BasicCommands;
import structures.GameState;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class SilverguardKnight extends Unit {
    
    public static final int initialHealth = 5;
    public static final int initialAttack = 1;

    protected int health;
    protected int attack;

    public SilverguardKnight() {
        super();
        health = 5;
        attack = 1;
    }

    public void performProvoke(ActorRef out, GameState gameState) {
        List<Unit> unitsToProvoke = gameState.unitsWithinAttackRange(this);
        for(Unit unit: unitsToProvoke) {
            unit.setProvoked(true);
            //gameState.isUnitProvoked(this) = true;
            //not sure if i should access it from unit class or gamestate class
        }
    }

    public void performZeal(ActorRef out, GameState gamestate) {
        this.setAttack(this.getAttack() + 2);
        BasicCommands.setUnitAttack(out, this, this.getAttack() + 2);
    }

    public SilverguardKnight getInstance(String configpaths) {
        return (SilverguardKnight)BasicObjectBuilders.loadUnit(configpaths, 14, SilverguardKnight.class);
    }
}
