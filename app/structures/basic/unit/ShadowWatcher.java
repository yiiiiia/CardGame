package structures.basic.unit;

import commands.BasicCommands;
import structures.GameState;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class ShadowWatcher extends Unit {
    
    public static final int initialHealth = 2;
    public static final int initialAttack = 3;

    protected int health;
    protected int attack;

    public ShadowWatcher() {
        super();
        health = 2;
        attack = 3;
    }

    public void performDeathWatch(ActorRef out, GameState gameState) {
        //whenever a unit, friendly or enemy dies
        this.setHealth(health + 1);
        this.setAttack(attack + 1);
        BasicCommands.setUnitHealth(out, this, health + 1);
        BasicCommands.setUnitAttack(out, this, attack + 1);
    }

    public static ShadowWatcher getInstance(String configpaths) {
        return (ShadowWatcher)BasicObjectBuilders.loadUnit(configpaths, 5, ShadowWatcher.class);
    }
}
